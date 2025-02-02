/*
 *  Copyright (c) 2002-2025, Manorrock.com. All Rights Reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      1. Redistributions of source code must retain the above copyright
 *         notice, this list of conditions and the following disclaimer.
 *
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package com.manorrock.aegean;

import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

/**
 * The Security Filter.
 * 
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class SecurityFilter implements Filter {

    /**
     * Stores the identity store.
     */
    @Inject
    private IdentityStore identityStore;

    /**
     * Initialize the filter.
     *
     * @param filterConfig the filter configuration
     * @throws ServletException if an error occurs during initialization
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Perform filtering on the request and response.
     *
     * This method checks if anonymous access is disabled by reading the "anonymousDisabled" 
     * context parameter. If anonymous access is disabled, it sends a 403 Forbidden response.
     * 
     * If the "Authorization" header is present and starts with "Basic ", it decodes the 
     * Base64-encoded credentials, extracts the username and password, and validates them 
     * using the IdentityStore. If the credentials are valid, it wraps the HttpServletRequest 
     * to provide the authenticated user's principal, roles, and remote user.
     *
     * Finally, it passes the request and response to the next filter in the chain.
     *
     * @param request the servlet request
     * @param response the servlet response
     * @param chain the filter chain
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String anonymousDisabled = httpRequest.getServletContext().getInitParameter("anonymousDisabled");
        if ("true".equalsIgnoreCase(anonymousDisabled)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Anonymous access is disabled");
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] values = credentials.split(":", 2);
            String username = values[0];
            String password = values[1];

            UsernamePasswordCredential credential = new UsernamePasswordCredential(username, password);
            CredentialValidationResult result = identityStore.validate(credential);

            if (result.getStatus() == CredentialValidationResult.Status.VALID) {
                httpRequest = new HttpServletRequestWrapper(httpRequest) {
                    @Override
                    public Principal getUserPrincipal() {
                        return result.getCallerPrincipal();
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        return result.getCallerGroups().contains(role);
                    }

                    @Override
                    public String getRemoteUser() {
                        return result.getCallerPrincipal().getName();
                    }
                };
            }
        }

        chain.doFilter(httpRequest, httpResponse);
    }

    /**
     * Destroy the filter.
     */
    @Override
    public void destroy() {
    }
}
