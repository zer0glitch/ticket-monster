/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.jdf.ticketmonster.test.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.InSequence;
import org.junit.Assert;
import org.junit.Test;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

/**
 * <p>
 * Simple test case that shows how to use PicketLink components to test your application security capabilities.
 * </p>
 * <p>
 * This test shows you how to use the {@link IdentityManager} to manage user information and credentials and also how to
 * authenticate them using the {@link Identity} component.
 * </p>
 * 
 * @author Pedro Silva
 * 
 */
public class AuthenticationTestCase extends AbstractSecurityTestCase {

    private static final String USER_LOGIN_NAME = "john";
    private static final String USER_PASSWORD = "letmein";

    @Test
    @InSequence(1)
    public void testCreateUser() throws Exception {
        SimpleUser john = new SimpleUser(USER_LOGIN_NAME);

        this.identityManager.add(john);

        assertNotNull(this.identityManager.getUser(john.getLoginName()));
    }

    @Test
    @InSequence(2)
    public void testPopulateCredentials() throws Exception {
        User john = this.identityManager.getUser(USER_LOGIN_NAME);

        assertNotNull(john);

        this.identityManager.updateCredential(john, new Password(USER_PASSWORD));
    }

    @Test
    @InSequence(3)
    public void testSuccessfulAuthentication() throws Exception {
        this.credentials.setUserId(USER_LOGIN_NAME);
        this.credentials.setCredential(new Password(USER_PASSWORD));

        assertFalse(this.identity.isLoggedIn());

        this.identity.login();

        assertTrue(this.identity.isLoggedIn());
    }

    @Test
    @InSequence(4)
    public void testUnSuccessfulAuthentication() throws Exception {
        this.credentials.setUserId(USER_LOGIN_NAME);
        this.credentials.setCredential(new Password("letmein2"));

        this.identity.login();

        Assert.assertFalse(this.identity.isLoggedIn());
    }

    @Test
    @InSequence(5)
    public void testLogout() throws Exception {
        this.credentials.setUserId(USER_LOGIN_NAME);
        this.credentials.setCredential(new Password(USER_PASSWORD));

        this.identity.login();

        assertTrue(this.identity.isLoggedIn());

        this.identity.logout();

        assertFalse(this.identity.isLoggedIn());
    }

}