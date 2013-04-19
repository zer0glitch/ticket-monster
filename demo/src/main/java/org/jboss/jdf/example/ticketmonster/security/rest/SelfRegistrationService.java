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

package org.jboss.jdf.example.ticketmonster.security.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.picketlink.idm.IdentityManagementException;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleGroup;
import org.picketlink.idm.model.SimpleRole;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

/**
 *
 */
@Path("/registration")
@Stateless
public class SelfRegistrationService {

    @Inject
    private IdentityManager identityManager;

    @Inject
    private LoginService loginService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityContext register(RegistrationRequest request) {
        SecurityContext securityContext = new SecurityContext();

        if (!request.getPassword().equals(request.getPasswordConfirmation())) {
            securityContext.setMessage("Password mismatch.");
        } else {
            try {
                if (this.identityManager.getUser(request.getEmail()) == null) {
                    performRegistration(request);
                    securityContext = performSilentAuthentication(request);
                } else {
                    securityContext.setMessage("This username is already in use. Try another one.");
                }
            } catch (IdentityManagementException ime) {
                securityContext.setMessage("Oops ! Registration failed, try it later.");
            }
        }
        
        return securityContext;
    }

    private SecurityContext performSilentAuthentication(RegistrationRequest request) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();

        authenticationRequest.setUsername(request.getEmail());
        authenticationRequest.setPassword(request.getPassword());

        return this.loginService.login(authenticationRequest);
    }

    private void performRegistration(RegistrationRequest request) {
        User newUser = new SimpleUser(request.getEmail());

        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());

        this.identityManager.add(newUser);
        
        Password password = new Password(request.getPassword());

        this.identityManager.updateCredential(newUser, password);

        Role userRole = this.identityManager.getRole("User");
        
        if (userRole == null) {
            userRole = new SimpleRole("User");
            this.identityManager.add(userRole);
        }

        Group userGroup = this.identityManager.getGroup("Users");

        if (userGroup == null) {
            userGroup = new SimpleGroup("Users");
            this.identityManager.add(userGroup);
        }

        this.identityManager.grantRole(newUser, userRole);
        this.identityManager.addToGroup(newUser, userGroup);
    }
}