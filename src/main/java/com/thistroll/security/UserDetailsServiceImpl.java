package com.thistroll.security;

import com.thistroll.domain.Session;
import com.thistroll.server.RequestValues;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * UserDetailsService to fetch a user by the user ID in the session in the RequestContext
 *
 * Created by MVW on 9/11/2017.
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = null;

        Session session = RequestValues.getSession();
        if (session != null) {
            userDetails = session.getUserDetails();
        }

        return userDetails;
    }
}
