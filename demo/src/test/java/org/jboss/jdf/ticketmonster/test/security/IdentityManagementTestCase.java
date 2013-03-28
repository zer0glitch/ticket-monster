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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleGroup;
import org.picketlink.idm.model.SimpleRole;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

/**
 * <p>
 * Simple test case that shows how to use PicketLink components to test your application security capabilities.
 * </p>
 * <p>
 * This test shows you how to use the {@link IdentityManager} to manage user, role and group information and also the
 * relationship between them.
 * </p>
 * 
 * @author Pedro Silva
 * 
 */
public class IdentityManagementTestCase extends AbstractSecurityTestCase {

    @Test
    @InSequence(1)
    public void testCreateUser() throws Exception {
        User john = new SimpleUser("john");

        john.setFirstName("John");
        john.setLastName("The Monster");
        john.setEmail("john@ticketmonster.org");
        
        super.identityManager.add(john);

        john = super.identityManager.getUser(john.getLoginName());
        
        assertNotNull(john);
        assertEquals("John", john.getFirstName());
        assertEquals("The Monster", john.getLastName());
        assertEquals("john@ticketmonster.org", john.getEmail());
    }

    @Test
    @InSequence(2)
    public void testCreateRole() throws Exception {
        Role administrator = new SimpleRole("Administrator");
        
        super.identityManager.add(administrator);
        
        assertNotNull(super.identityManager.getRole(administrator.getName()));
    }

    @Test
    @InSequence(3)
    public void testCreateGroup() throws Exception {
        Group administrators = new SimpleGroup("Administrators");
        
        super.identityManager.add(administrators);
        
        assertNotNull(super.identityManager.getGroup(administrators.getName()));
    }

    @Test
    @InSequence(4)
    public void testAssociateUserWithRole() throws Exception {
        User john = super.identityManager.getUser("john");
        Role administrator = super.identityManager.getRole("Administrator");
        
        super.identityManager.grantRole(john, administrator);
        
        assertTrue(super.identityManager.hasRole(john, administrator));
    }

    @Test
    @InSequence(5)
    public void testAssociateUserWithGroup() throws Exception {
        User john = super.identityManager.getUser("john");
        Group administrators = super.identityManager.getGroup("Administrators");
        
        super.identityManager.addToGroup(john, administrators);
        
        assertTrue(super.identityManager.isMember(john, administrators));
    }

    @Test
    @InSequence(6)
    public void testAssociateUserWithGroupAndRole() throws Exception {
        User john = super.identityManager.getUser("john");
        Role manager = new SimpleRole("Manager");
        Group sales = new SimpleGroup("Sales");
        
        super.identityManager.add(manager);
        super.identityManager.add(sales);
        
        // john is now a Manager of the Sales group
        super.identityManager.grantGroupRole(john, manager, sales);
        
        assertTrue(super.identityManager.hasGroupRole(john, manager, sales));
    }

}