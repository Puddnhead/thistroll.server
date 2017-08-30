package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.thistroll.data.api.UserRepository;
import com.thistroll.data.exceptions.DuplicateUsernameException;
import com.thistroll.domain.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

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
                .withString(User.PASSWORD_PROPERTY, DigestUtils.sha256Hex(password)));

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
        Table table = getUserTable();
        Index index = table.getIndex(User.USERNAME_INDEX);
        QuerySpec spec = new QuerySpec()
                .withHashKey(User.PARTITION_KEY_NAME, User.PARTITION_KEY_VALUE)
                .withRangeKeyCondition(new RangeKeyCondition(User.USERNAME_PROPERTY).eq(username));
        ItemCollection<QueryOutcome> items = index.query(spec);
        if (!items.iterator().hasNext()) {
            return null;
        }
        Item item = items.iterator().next();
        return UserMapper.mapItemToUser(item);
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public void deleteUser(String id) {

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

    @Required
    public void setConnectionProvider(DynamoDBConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
}
