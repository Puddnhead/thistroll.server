package com.thistroll.domain;

import com.google.common.collect.Sets;
import com.thistroll.domain.enums.UserRole;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit test for {@link User}
 *
 * Created by MVW on 8/22/2017.
 */
public class UserTest {

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(User.class).withRedefinedSuperclass().verify();
    }

    @Test
    public void testGetGrantedAuthorities() throws Exception {
        User user = new User.Builder()
                .roles(Sets.newHashSet(UserRole.ADMIN, UserRole.USER))
                .build();
        GrantedAuthority adminAuthority = new SimpleGrantedAuthority(UserRole.ADMIN.name());
        GrantedAuthority userAuthority = new SimpleGrantedAuthority(UserRole.USER.name());
        assertThat(user.getAuthorities().contains(adminAuthority), is(true));
        assertThat(user.getAuthorities().contains(userAuthority), is(true));
    }

}