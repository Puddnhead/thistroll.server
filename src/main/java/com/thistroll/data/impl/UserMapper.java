package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.UserRole;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Static utility class for mapping users
 *
 * Created by MVW on 8/26/2017.
 */
class UserMapper {

    static User mapItemToUser(Item item) {
        if (item == null) {
            return null;
        }

        User.Builder builder = new User.Builder()
                .id(item.getString(User.ID_PROPERTY))
                .username(item.getString(User.USERNAME_PROPERTY))
                .firstName(item.getString(User.FIRST_NAME_PROPERTY))
                .lastName(item.getString(User.LAST_NAME_PROPERTY))
                .email(item.getString(User.EMAIL_PROPERTY))
                .notificationsEnabled(item.getBoolean(User.NOTIFICATIONS_PROPERTY));
        if (item.isPresent(User.CREATED_ON_PROPERTY) && !item.isNull(User.CREATED_ON_PROPERTY)) {
            builder = builder.createdOn(new DateTime(item.getLong(User.CREATED_ON_PROPERTY)));
        }
        if (item.isPresent(User.LAST_UPDATED_ON_PROPERTY) && !item.isNull(User.LAST_UPDATED_ON_PROPERTY)) {
            builder = builder.lastUpdatedOn(new DateTime(item.getLong(User.LAST_UPDATED_ON_PROPERTY)));
        }
        if (item.isPresent(User.ROLES_PROPERTY) && !item.isNull(User.ROLES_PROPERTY)) {
            builder = builder.roles(UserMapper.deserializeRoles(item.getString(User.ROLES_PROPERTY)));
        }
        return builder.build();
    }

    static List<User> mapItemsToUsers(List<Map<String, AttributeValue>> items) {
        return items.stream()
                .map(UserMapper::mapAttributeMapToUser)
                .collect(Collectors.toList());
    }

    static User mapAttributeMapToUser(Map<String, AttributeValue> attributeValueMap) {
        User.Builder builder = new User.Builder()
                .id(attributeValueMap.get(User.ID_PROPERTY).getS())
                .username(attributeValueMap.get(User.USERNAME_PROPERTY).getS())
                .email(attributeValueMap.get(User.EMAIL_PROPERTY).getS())
                .notificationsEnabled(attributeValueMap.get(User.NOTIFICATIONS_PROPERTY).getBOOL());
        if (attributeValueMap.containsKey(User.FIRST_NAME_PROPERTY)) {
            builder = builder.firstName(attributeValueMap.get(User.FIRST_NAME_PROPERTY).getS());
        }
        if (attributeValueMap.containsKey(User.LAST_NAME_PROPERTY)) {
            builder = builder.lastName((attributeValueMap.get(User.LAST_NAME_PROPERTY).getS()));
        }
        AttributeValue createdOnProperty = attributeValueMap.get(User.CREATED_ON_PROPERTY);
        if (createdOnProperty != null) {
            builder = builder.createdOn(new DateTime(createdOnProperty.getL()));
        }
        AttributeValue lastUpdatedOnProperty = attributeValueMap.get(User.LAST_UPDATED_ON_PROPERTY);
        if (lastUpdatedOnProperty != null) {
            builder = builder.lastUpdatedOn(new DateTime(lastUpdatedOnProperty.getL()));
        }
        AttributeValue rolesProperty = attributeValueMap.get(User.ROLES_PROPERTY);
        if (rolesProperty != null) {
            builder = builder.roles(UserMapper.deserializeRoles(rolesProperty.getS()));
        }
        return builder.build();
    }

    /**
     * Serializes a set or roles to a comma-delimited string
     *
     * @param roles roles to serialize
     * @return a comma-delimited string
     */
    static String serializeRoles(Set<UserRole> roles) {
        String serialized = "";
        boolean first = true;

        for (UserRole role: roles) {
            if (first) {
                first = false;
            } else {
                serialized += ",";
            }
            serialized += role.name();
        }
        return serialized;
    }

    /**
     * Given a comma-delimited list of roles, deserializes them to a Set of UserRoles
     *
     * @param roles a comma-delimited list of roles
     * @return a set of UserRoles
     */
    static Set<UserRole> deserializeRoles(String roles) {
        List<String> rolesList = Arrays.asList(roles.split(","));
        return rolesList.stream()
                .map(UserRole::valueOf)
                .collect(Collectors.toSet());
    }
}
