package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.thistroll.domain.Blog;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.UserRole;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by MVW on 8/26/2017.
 */
public class UserMapper {

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
