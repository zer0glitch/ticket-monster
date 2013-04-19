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

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.jdf.example.ticketmonster.security.model.SecurityContext;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.User;

/**
 *
 */
@Named
@Path ("/userinfo")
@SessionScoped
public class UserInfoService implements Serializable {

    private static final long serialVersionUID = -5768792621245392562L;
    
    @Inject
    private Identity identity;
    
    @Inject
    private IdentityManager identityManager;
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityContext info() {
        SecurityContext context = new SecurityContext();
        
        if (this.identity.isLoggedIn()) {
            context.setUser((User) this.identity.getUser());
            context.setAdministrator(isAdmin());
        }
        
        return context;
    }
    
    public boolean isAdmin() {
        if (this.identity.isLoggedIn()) {
            return this.identityManager.hasRole(this.identity.getUser(), this.identityManager.getRole("Administrator"));
        }
        
        return false;
    }

    public void logout() {
        this.identity.logout();
    }

    public boolean isLoggedIn() {
        return this.identity.isLoggedIn();
    }
}