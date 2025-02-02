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
 *         notice, this list of conditions and the following disclaimer in
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

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.servlet.ServletContext;
import jakarta.security.enterprise.CallerPrincipal;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * The Security IdentityStore.
 * 
 * @author Manfred Riem (mriem@manorrock.com)
 */
@ApplicationScoped
public class SecurityIdentityStore implements IdentityStore {

    /**
     * Stores the admin username.
     */
    private String adminUsername;

    /**
     * Stores the admin password.
     */
    private String adminPassword;

    /**
     * Stores the ServletContext.
     */
    @Inject
    private ServletContext servletContext;

    /**
     * Stores the logger.
     */
    private static final Logger LOGGER = Logger.getLogger(SecurityIdentityStore.class.getName());

    /**
     * Initialize the identity store.
     * <p>
     * This method is called after the bean's properties have been initialized.
     * It retrieves the admin username and password from the servlet context's
     * initialization parameters and logs the initialization status.
     */
    @PostConstruct
    public void init() {
        adminUsername = servletContext.getInitParameter("adminUsername");
        adminPassword = servletContext.getInitParameter("adminPassword");
        if (adminUsername != null && !adminUsername.isEmpty()) {
            LOGGER.info("SecurityIdentityStore initialized with adminUsername: " + adminUsername);
        }
        if (adminPassword != null && !adminPassword.isEmpty()) {
            LOGGER.info("SecurityIdentityStore initialized with adminPassword: " + "********");
        }
    }

    /**
     * Validate the provided credential.
     * <p>
     * This method checks if the provided credential matches the admin username
     * and password. If they match, it returns a valid CredentialValidationResult
     * with the admin role. Otherwise, it returns a not validated result.
     *
     * @param credential the credential to validate
     * @return the result of the credential validation
     */
    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (adminUsername == null || adminUsername.isEmpty() || adminPassword == null || adminPassword.isEmpty()) {
            return CredentialValidationResult.NOT_VALIDATED_RESULT;
        }
        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential usernamePasswordCredential = (UsernamePasswordCredential) credential;
            if (adminUsername.equals(usernamePasswordCredential.getCaller()) &&
                adminPassword.equals(usernamePasswordCredential.getPasswordAsString())) {
                return new CredentialValidationResult(
                    new CallerPrincipal(adminUsername),  Collections.singleton("admin")
                );
            }
        }
        return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }
}
