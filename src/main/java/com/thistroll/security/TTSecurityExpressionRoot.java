package com.thistroll.security;

import com.thistroll.domain.Session;
import com.thistroll.domain.enums.UserRole;
import com.thistroll.server.RequestValues;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Security expression root to add custom security expressions
 *
 * Created by MVW on 9/11/2017.
 */
public class TTSecurityExpressionRoot
        extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    TTSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    /**
     * Returns true if the user making the request has the ADMIN role
     *
     * @return true if the user in the request context has the ADMIN role
     */
    public boolean isAdmin() {
        boolean isAdmin = false;

        Session session = RequestValues.getSession();
        if (session != null && session.getUserDetails() != null) {
            isAdmin = session.getUserDetails().getRoles().contains(UserRole.ADMIN);
        }

        return isAdmin;
    }

    @Override
    public void setFilterObject(Object filterObject) {

    }

    @Override
    public Object getFilterObject() {
        return null;
    }

    @Override
    public void setReturnObject(Object returnObject) {

    }

    @Override
    public Object getReturnObject() {
        return null;
    }

    @Override
    public Object getThis() {
        return null;
    }
}
