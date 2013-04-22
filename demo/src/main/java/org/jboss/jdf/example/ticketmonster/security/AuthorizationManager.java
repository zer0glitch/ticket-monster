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

package org.jboss.jdf.example.ticketmonster.security;

import java.lang.annotation.Annotation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.picketlink.Identity;
import org.picketlink.deltaspike.Secures;
import org.picketlink.idm.IdentityManager;

/**
 * <p>
 * Provides some authorization services for the application like custom {@link Annotation} to secure proteced resources.
 * </p>
 * 
 * @author Pedro Silva
 * 
 */
@Named
@ApplicationScoped
public class AuthorizationManager {

    @Inject
    private Instance<Identity> identity;

    @Inject
    private Instance<IdentityManager> identityManager;

    /**
     * <p>
     * Check if a method or type annotated with the {@link UserLoggedIn} is being access by an authenticated user. This method
     * is called before the annotated method is called.
     * </p>
     * 
     * @param identity
     * @return
     */
    @Secures
    @UserLoggedIn
    public boolean isUserLoggedIn(Identity identity) {
        return identity.isLoggedIn();
    }

    public boolean isAdmin() {
        Identity identity = this.identity.get();
        
        if (isUserLoggedIn(identity)) {
            IdentityManager identityManager = this.identityManager.get();
            
            return identityManager.hasRole(identity.getUser(), identityManager.getRole("Administrator"));
        }
        
        return false;
    }
}