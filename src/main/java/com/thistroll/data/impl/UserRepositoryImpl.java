package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.thistroll.data.api.UserRepository;
import com.thistroll.data.mappers.UserMapper;
import com.thistroll.data.util.DynamoDBConnectionProvider;
import com.thistroll.data.util.UserValidationUtil;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.exceptions.DuplicateEmailException;
import com.thistroll.exceptions.DuplicateUsernameException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * Implementation class for {@link UserRepository}
 *
 * Created by MVW on 8/26/2017.
 */
public class UserRepositoryImpl implements UserRepository {

    private DynamoDBConnectionProvider connectionProvider;

    private static final String TABLE_NAME = "thistroll_user";
    private static final int DEFAULT_PAGE_SIZE = 50;

    /**
     * In-memory user cache
     */
    private Cache<String, User> userCache = null;

    @Override
    public User createUser(User user, String password) {
        // These validation methods throw runtime exceptions
        UserValidationUtil.validateUser(user);
        UserValidationUtil.validatePassword(password);

        if (getUserByUsername(user.getUsername()) != null) {
            throw new DuplicateUsernameException("Create failed because a user already exists with username " + user.getUsername());
        }

        if (getUserByEmail(user.getEmail()) != null) {
            throw new DuplicateEmailException("Create failed because a user already exists with email " + user.getEmail());
        }

        Table userTable = getUserTable();
        User createdUser = createUserWithGeneratedIdAndDates(user);

        // Required properties
        Item item = new Item().withPrimaryKey(User.PARTITION_KEY_NAME, User.PARTITION_KEY_VALUE, User.ID_PROPERTY, createdUser.getId())
                .withLong(User.CREATED_ON_PROPERTY, createdUser.getCreatedOn().getMillis())
                .withLong(User.LAST_UPDATED_ON_PROPERTY, createdUser.getLastUpdatedOn().getMillis())
                .withString(User.USERNAME_PROPERTY, createdUser.getUsername())
                .withString(User.ROLES_PROPERTY, UserMapper.serializeRoles(createdUser.getRoles()))
                .withString(User.EMAIL_PROPERTY, createdUser.getEmail())
                .withBoolean(User.NOTIFICATIONS_PROPERTY, createdUser.isNotificationsEnabled())
                .withString(User.PASSWORD_PROPERTY, hashPassword(password));

        // Optional properties
        if (StringUtils.isNotEmpty(createdUser.getFirstName())) {
            item = item.withString(User.FIRST_NAME_PROPERTY, createdUser.getFirstName());
        }
        if (StringUtils.isNotEmpty(createdUser.getLastName())) {
            item = item.withString(User.LAST_NAME_PROPERTY, createdUser.getLastName());
        }

        userTable.putItem(item);

        getUserCache().put(createdUser.getId(), createdUser);

        return createdUser;
    }

    @Override
    public User getUserById(String id) {
        Cache<String, User> cache = getUserCache();

        // Check cache first
        User user = cache.getIfPresent(id);
        if (user != null) {
            return user;
        }

        // Otherwise make a DB call
        Table table = getUserTable();
        GetItemSpec spec = new GetItemSpec().withPrimaryKey(User.PARTITION_KEY_NAME, User.PARTITION_KEY_VALUE,
                User.ID_PROPERTY, id);
        Item outcome = table.getItem(spec);
        if (outcome == null) {
            return null;
        }
        user = UserMapper.mapItemToUser(outcome);
        cache.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        Item item = getUserItemByUsername(username);
        if (item == null) {
            return null;
        }
        User user = UserMapper.mapItemToUser(item);
        getUserCache().put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        Table table = getUserTable();
        Index index = table.getIndex(User.EMAIL_INDEX);
        QuerySpec spec = new QuerySpec()
                .withHashKey(User.PARTITION_KEY_NAME, User.PARTITION_KEY_VALUE)
                .withRangeKeyCondition(new RangeKeyCondition(User.EMAIL_PROPERTY).eq(email));
        ItemCollection<QueryOutcome> items = index.query(spec);
        if (!items.iterator().hasNext() || items.iterator().next() == null) {
            return null;
        }

        User user = UserMapper.mapItemToUser(items.iterator().next());
        getUserCache().put(user.getId(), user);
        return user;
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

        User updatedUser = getUserById(user.getId());
        getUserCache().put(updatedUser.getId(), updatedUser);

        return updatedUser;
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

        getUserCache().invalidate(id);
        return Outcome.SUCCESS;
    }

    @Override
    public User getUserWithCredentials(String username, String password) {
        Item item = getUserItemByUsername(username);
        if (item != null) {
            String hashedPassword = item.getString(User.PASSWORD_PROPERTY);
            // If there is a user with the given username and their password matches the provided password
            if (StringUtils.isNotEmpty(hashedPassword) && hashedPassword.equals(hashPassword(password))) {
                User user = UserMapper.mapItemToUser(item);
                getUserCache().put(user.getId(), user);
                return user;
            }
        }

        // Else return null
        return null;
    }

    @Override
    public List<User> getAllUsers(Optional<Integer> pageNumber, Optional<Integer> pageSize) {
        List<User> users = new ArrayList<>();
        Integer lastUser = null;
        int actualPageSize = pageSize.orElse(DEFAULT_PAGE_SIZE);

        if (pageNumber.isPresent()) {
            lastUser = pageNumber.get() * actualPageSize + actualPageSize;
        }

        Cache<String, User> cache = getUserCache();
        Map<String, AttributeValue> lastEvaluatedKey;
        AmazonDynamoDB dynamoDB = connectionProvider.getAmazonDynamoDB();
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(TABLE_NAME);

        do {
            ScanResult result = dynamoDB.scan(scanRequest);
            lastEvaluatedKey = result.getLastEvaluatedKey();
            scanRequest = scanRequest.withExclusiveStartKey(lastEvaluatedKey);

            List<User> pageOfUsers = UserMapper.mapItemsToUsers(result.getItems());
            users.addAll(pageOfUsers);
            pageOfUsers.forEach(user -> cache.put(user.getId(), user));
            // go until we've fetched all users or as many as have been requested
        } while (lastEvaluatedKey != null && (lastUser == null || lastUser >= users.size()));

        if (pageNumber.isPresent()) {
            int startIndex = pageNumber.get() * actualPageSize;
            int endIndex = (startIndex + actualPageSize) > users.size()
                    ? users.size()
                    : startIndex + actualPageSize;

            if (startIndex < users.size()) {
                users = users.subList(startIndex, endIndex);
            } else {
                users.clear();
            }
        }

        return users;
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

    private Cache<String, User> getUserCache() {
        if (userCache != null) {
            return userCache;
        }

        return CacheBuilder.newBuilder()
                .maximumSize(500)
                .build();
    }

    static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }

    @Required
    public void setConnectionProvider(DynamoDBConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
}
