package com.thistroll.data.impl;

import com.google.common.collect.Sets;
import com.thistroll.data.mappers.UserMapper;
import com.thistroll.domain.enums.UserRole;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Unit test for {@link UserMapper}
 *
 * Created by MVW on 8/26/2017.
 */
public class UserMapperTest {

    @Test
    public void testRoleSerialization() throws Exception {
        Set<UserRole> provided = Sets.newHashSet(UserRole.ADMIN, UserRole.USER);
        String serialized = UserMapper.serializeRoles(provided);
        Set<UserRole> deserialized = UserMapper.deserializeRoles(serialized);
        assertThat(provided, is(deserialized));
    }

}