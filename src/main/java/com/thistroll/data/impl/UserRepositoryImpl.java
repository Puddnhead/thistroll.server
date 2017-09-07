package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.thistroll.data.api.UserRepository;
import com.thistroll.data.exceptions.DuplicateUsernameException;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by MVW on 8/26/2017.
 */
public class UserRepositoryImpl implements UserRepository {

    private DynamoDBConnectionProvider connectionProvider;

    private static final String TABLE_NAME = "thistroll_user";

    @Override
    public User createUser(User user, String password) {
        // These validation methods throw runtime exceptions
        UserValidationUtil.validateUser(user);
        UserValidationUtil.validatePassword(password);

        if (getUserByUsername(user.getUsername()) != null) {
            throw new DuplicateUsernameException();
        }

        Table userTable = getUserTable();
        User createdUser = createUserWithGeneratedIdAndDates(user);

        userTable.putItem(new Item().withPrimaryKey(User.PARTITION_KEY_NAME, User.PARTITION_KEY_VALUE, User.ID_PROPERTY, createdUser.getId())
                .withLong(User.CREATED_ON_PROPERTY, createdUser.getCreatedOn().getMillis())
                .withLong(User.LAST_UPDATED_ON_PROPERTY, createdUser.getLastUpdatedOn().getMillis())
                .withString(User.USERNAME_PROPERTY, createdUser.getUsername())
                .withString(User.FIRST_NAME_PROPERTY, createdUser.getFirstName())
                .withString(User.LAST_NAME_PROPERTY, createdUser.getLastName())
                .withString(User.ROLES_PROPERTY, UserMapper.serializeRoles(createdUser.getRoles()))
                .withString(User.EMAIL_PROPERTY, createdUser.getEmail())
                .withBoolean(User.NOTIFICATIONS_PROPERTY, createdUser.isNotificationsEnabled())
                .withString(User.PASSWORD_PROPERTY, hashPassword(password)));

        return createdUser;
    }

    @Override
    public User getUserById(String id) {
        Table table = getUserTable();

        GetItemSpec spec = new GetItemSpec().withPrimaryKey(User.PARTITION_KEY_NAME, User.PARTITION_KEY_VALUE,
                User.ID_PROPERTY, id);
        Item outcome = table.getItem(spec);

        return UserMapper.mapItemToUser(outcome);
    }

    @Override
    public User getUserByUsername(String username) {
        Item item = getUserItemByUsername(username);
        if (item == null) {
            return null;
        }
        return UserMapper.mapItemToUser(item);
    }

    @Override
    public User updateUser(User user) {
        Table table = getUserTable();

        List<AttributeUpdate> attributeUpdates = new ArrayList<>();
        attributeUpdates.add(new AttributeUpdate(User.LAST_UPDATED_ON_PROPERTY).put(user.getLastUpdatedOn().getMillis()));
        attributeUpdates.add(new AttributeUpdate(User.NOTIFICATIONS_PROPERTY).put(user.isNotificationsEnabled()));
        if (StringUtils.isNotEmpty(user.getEmail())) {
            attributeUpdates.add(new AttributeUpdate(User.EMAIL_PROPERTY).put(user.getEmail()));
        }
        if (StringUtils.isNotEmpty(user.getFirstName())) {
            attributeUpdates.add(new AttributeUpdate(User.FIRST_NAME_PROPERTY).put(user.getFirstName()));
        }
        if (StringUtils.isNotEmpty(user.getLastName())) {
            attributeUpdates.add(new AttributeUpdate(User.LAST_NAME_PROPERTY).put(user.getLastName()));
        }

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey(User.PARTITION_KEY_NAME, User.PARTITION_KEY_VALUE, User.ID_PROPERTY, user.getId())
                .withAttributeUpdate(attributeUpdates);
        table.updateItem(updateItemSpec);

        return getUserById(user.getId());
    }

    @Override
    public Outcome deleteUser(String id) {
        Table table = getUserTable();
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(User.PARTITION_KEY_NAME, User.PARTITION_KEY_VALUE, User.ID_PROPERTY, id)
                .withReturnValues(ReturnValue.ALL_OLD);
        DeleteItemOutcome outcome = table.deleteItem(deleteItemSpec);
        if (outcome.getDeleteItemResult().getAttributes() == null) {
            return Outcome.FAILURE;
        }
        return Outcome.SUCCESS;
    }

    @Override
    public User getUserWithCredentials(String username, String password) {
        Item item = getUserItemByUsername(username);
        if (item != null) {
            String hashedPassword = item.getString(User.PASSWORD_PROPERTY);
            // If there is a user with the given username and their password matches the provided password
            if (StringUtils.isNotEmpty(hashedPassword) && hashedPassword.equals(hashPassword(password))) {
                return UserMapper.mapItemToUser(item);
            }
        }

        // Else return null
        return null;
    }

    private Item getUserItemByUsername(String username) {
        Table table = getUserTable();
        Index index = table.getIndex(User.USERNAME_INDEX);
        QuerySpec spec = new QuerySpec()
                .withHashKey(User.PARTITION_KEY_NAME, User.PARTITION_KEY_VALUE)
                .withRangeKeyCondition(new RangeKeyCondition(User.USERNAME_PROPERTY).eq(username));
        ItemCollection<QueryOutcome> items = index.query(spec);
        if (!items.iterator().hasNext()) {
            return null;
        }

        return items.iterator().next();
    }

    private User createUserWithGeneratedIdAndDates(User user) {
        DateTime now = new DateTime();

        return new User.Builder()
                .id(UUID.randomUUID().toString())
                .createdOn(now)
                .lastUpdatedOn(now)
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .notificationsEnabled(user.isNotificationsEnabled())
                .roles(user.getRoles())
                .build();
    }

    private Table getUserTable() {
        DynamoDB dynamoDB = connectionProvider.getDynamoDB();
        return dynamoDB.getTable(TABLE_NAME);
    }

    static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }

    @Required
    public void setConnectionProvider(DynamoDBConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
}
