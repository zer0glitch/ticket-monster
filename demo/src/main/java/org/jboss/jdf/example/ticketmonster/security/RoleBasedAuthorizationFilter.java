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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketlink.Identity;
import org.picketlink.deltaspike.security.api.authorization.AccessDeniedException;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Role;

/**
 * <p>A RBAC {@link Filter} that can be used to protected web resources based on a simple role mapping.</p>
 * <p>This filter accepts two params:</p>
 * <ul>
 *  <li>protectedResources: A string with a list of url patterns and roles for access contro. Eg.: /admin/*: Administrator!AnotherRole, /someUri:SomeRole</li>
 *  <li>loginUri: The login page URI.</li>
 * </ul>
 * 
 * @author Pedro Silva
 *
 */
public class RoleBasedAuthorizationFilter implements Filter {

    private static final String ANY_RESOURCE_PATTERN = "*";

    @Inject
    private Instance<Identity> identity;
    
    @Inject
    private Instance<IdentityManager> identityManager;

    private Map<String, String[]> urlPatternRoles = new HashMap<String, String[]>();

    private String loginUri;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String protectedResources = filterConfig.getInitParameter("protectedResources");
        
        if (protectedResources != null) {
            String[] resources = protectedResources.split(",");
            
            for (String resource : resources) {
                String[] config = resource.split(":");
                this.urlPatternRoles.put(config[0], config[1].split("!"));
            }
        }
        
        this.loginUri = filterConfig.getInitParameter("loginUri");
        
        if (this.loginUri == null) {
            this.loginUri = "/#login";
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            String requestURI = httpRequest.getRequestURI();
            
            Set<Entry<String, String[]>> entrySet = this.urlPatternRoles.entrySet();
            
            for (Entry<String, String[]> entry : entrySet) {
                if (matches(entry.getKey(), requestURI)) {
                    if (!getIdentity().isLoggedIn()) {
                        httpResponse.sendRedirect(httpRequest.getContextPath() + this.loginUri);                    
                    } else {
                        String[] roles = entry.getValue();
                        
                        for (String roleName : roles) {
                            Role role = getIdentityManager().getRole(roleName.trim());
                            
                            if (role == null) {
                                throw new IllegalStateException("The specified role does not exists [" + role + "]. Check your configuration.");
                            }
                            
                            if (!getIdentityManager().hasRole(getIdentity().getUser(), role)) {
                                handleAccessDeniedError(httpResponse);
                            }
                        }
                    } 
                }
            }
            
            chain.doFilter(httpRequest, httpResponse);
        } catch (AccessDeniedException ade) {
            handleAccessDeniedError(httpResponse);
        } catch (Exception e) {
            if (AccessDeniedException.class.isInstance(e.getCause())) {
                handleAccessDeniedError(httpResponse);
            } else {
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);    
            }
        }
    }

    @Override
    public void destroy() {
    }

    private Identity getIdentity() {
        return this.identity.get();
    }
    
    private IdentityManager getIdentityManager() {
        return this.identityManager.get();
    }

    private void handleAccessDeniedError(HttpServletResponse httpResponse) throws IOException {
        if (!getIdentity().isLoggedIn()) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
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
}
