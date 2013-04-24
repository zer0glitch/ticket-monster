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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.picketlink.Identity;
import org.picketlink.deltaspike.Secures;
import org.picketlink.deltaspike.security.api.authorization.AccessDeniedException;
import org.picketlink.deltaspike.security.api.authorization.SecurityViolation;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Role;

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
    
    private static final String ANY_RESOURCE_PATTERN = "*";
    
    private Map<String, String[]> roleProtectedResources = new HashMap<String, String[]>();
    
    @Inject
    private Instance<Identity> identity;

    @Inject
    private Instance<IdentityManager> identityManager;

    @PostConstruct
    public void init() {
        this.roleProtectedResources.put("/admin/*", new String[] {"Administrator"});
    }
    
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
        Identity identity = getIdentity();
        
        if (isUserLoggedIn(identity)) {
            IdentityManager identityManager = getIdentityManager();
            
            return identityManager.hasRole(identity.getUser(), identityManager.getRole("Administrator"));
        }
        
        return false;
    }

    public void isAllowed(HttpServletRequest httpRequest) throws UserNotLoggedInException, AccessDeniedException {
        final String requestURI = httpRequest.getRequestURI();
        
        Set<Entry<String, String[]>> entrySet = this.roleProtectedResources.entrySet();
        
        for (Entry<String, String[]> entry : entrySet) {
            if (matches(entry.getKey(), requestURI)) {
                if (!getIdentity().isLoggedIn()) {
                    throw new UserNotLoggedInException();
                } else {
                    String[] roles = entry.getValue();
                    
                    for (String roleName : roles) {
                        Role role = getIdentityManager().getRole(roleName.trim());
                        
                        if (role == null) {
                            throw new IllegalStateException("The specified role does not exists [" + role + "]. Check your configuration.");
                        }
                        
                        if (!getIdentityManager().hasRole(getIdentity().getUser(), role)) {
                            HashSet<SecurityViolation> violations = new HashSet<SecurityViolation>();
                            
                            violations.add(new SecurityViolation() {
                                
                                private static final long serialVersionUID = 1L;

                                @Override
                                public String getReason() {
                                    return "Access denied for resource [" + requestURI + "]";
                                }
                            });
                            
                            throw new AccessDeniedException(violations);
                        }
                    }
                } 
            }
        }
    }
    
    /**
     * <p>
     * Checks if the provided URI matches the specified pattern.
     * </p>
     *
     * @param uri
     * @param pattern 
     * @return
     */
    private boolean matches(String pattern, String uri) {
        if (pattern.equals(ANY_RESOURCE_PATTERN)) {
            return true;
        }

        if (pattern.equals(uri)) {
            return true;
        }

        if (pattern.endsWith(ANY_RESOURCE_PATTERN)) {
            String formattedPattern = pattern.replaceAll("/[*]", "/");

            if (uri.contains(formattedPattern)) {
                return true;
            }
        }

        if (pattern.equals("*")) {
            return true;
        } else {
            return (pattern.startsWith(ANY_RESOURCE_PATTERN) && uri.endsWith(pattern.substring(
                    ANY_RESOURCE_PATTERN.length() + 1, pattern.length())));
        }
    }
    
    private IdentityManager getIdentityManager() {
        return this.identityManager.get();
    }

    private Identity getIdentity() {
        return this.identity.get();
    }

}