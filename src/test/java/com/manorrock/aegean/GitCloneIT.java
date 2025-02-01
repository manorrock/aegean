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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.transport.TransportHttp;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The Git clone integration test.
 * 
 * This class contains integration tests for cloning Git repositories using
 * JGit. It includes tests for cloning as an admin user, cloning a non-existent
 * repository, and creating and accessing a repository anonymously.
 * 
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class GitCloneIT {

    /**
     * Stores the logger.
     */
    private static final Logger LOGGER = Logger.getLogger(GitCloneIT.class.getName());

    /**
     * Test cloning a repository as an admin user.
     * 
     * This test sets up a local path for the repository, encodes the admin
     * credentials, and attempts to clone the repository from the specified
     * URI. It verifies that the repository was cloned successfully by checking
     * the existence of the repository directory.
     * 
     * Note: The extra HTTP header functionality is necessary as the server will not
     * issue a 403 for a non-existent repository, so we must send the Basic auth
     * header forcefully without being asked for it.
     * 
     * @throws GitAPIException if an error occurs during the clone operation
     * @throws IOException     if an I/O error occurs
     */
    @Test
    public void testCloneRepositoryAsAdmin() throws GitAPIException, IOException {
        File localPath = new File("target/repos/admin");
        localPath.getParentFile().mkdirs();

        String uri = "http://localhost:8080/aegean/repositories/admin.git";
        String adminUsername = System.getProperty("adminUsername");
        String adminPassword = System.getProperty("adminPassword");

        if (adminUsername == null || adminPassword == null) {
            LOGGER.warning("System properties adminUsername or adminPassword are not set.");
            throw new IllegalStateException("Admin credentials are not set.");
        }

        String credentials = adminUsername + ":" + adminPassword;
        String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        String extraHeader = "Basic " + base64Credentials;

        LOGGER.info("Cloning repository from " + uri + " to " + localPath.getAbsolutePath());

        try (Git result = Git.cloneRepository()
                .setURI(uri)
                .setDirectory(localPath)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(adminUsername, adminPassword))
                .setTransportConfigCallback(transport -> {
                    if (transport instanceof TransportHttp) {
                        ((TransportHttp) transport)
                                .setAdditionalHeaders(Collections.singletonMap("Authorization", extraHeader));
                    }
                })
                .call()) {
            LOGGER.info("Repository cloned successfully");
            assert result.getRepository().getDirectory().exists();
        } catch (GitAPIException e) {
            LOGGER.log(Level.SEVERE, "Failed to clone repository: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Test cloning a non-existent repository anonymously.
     * 
     * This test attempts to clone a repository that does not exist and expects a
     * GitAPIException to be thrown. It verifies that the exception is thrown by
     * using the assertThrows method.
     */
    @Test
    public void testCloneNonExistentRepositoryAnonymously() {
        File localPath = new File("target/repos/doesnotexist");
        localPath.getParentFile().mkdirs();

        LOGGER.info("Attempting to clone non-existent repository to " + localPath.getAbsolutePath());

        assertThrows(GitAPIException.class, () -> {
            Git.cloneRepository()
                    .setURI("http://localhost:8080/aegean/repositories/doesnotexist.git")
                    .setDirectory(localPath)
                    .call();
        });
    }

    /**
     * Test creating a repository as an admin user and then accessing it
     * anonymously.
     * 
     * This test first creates a repository as an admin user by cloning it from the
     * specified URI. It then attempts to access the same repository anonymously by
     * cloning it again to a different local path. The test verifies that both the
     * creation and access operations are successful bychecking the existence of the
     * repository directories.
     * 
     * Note: The extra HTTP header functionality is necessary as the server will not
     * issue a 403 for a non-existent repository, so we must send the Basic auth
     * header forcefully without being asked for it.
     * 
     * @throws GitAPIException if an error occurs during the clone operation
     * @throws IOException     if an I/O error occurs
     */
    @Test
    public void testCreateAndAccessRepositoryAnonymously() throws GitAPIException, IOException {
        File adminLocalPath = new File("target/repos/admin-create");
        adminLocalPath.getParentFile().mkdirs();

        String uri = "http://localhost:8080/aegean/repositories/admin-create.git";
        String adminUsername = System.getProperty("adminUsername");
        String adminPassword = System.getProperty("adminPassword");

        if (adminUsername == null || adminPassword == null) {
            LOGGER.warning("System properties adminUsername or adminPassword are not set.");
            throw new IllegalStateException("Admin credentials are not set.");
        }

        String credentials = adminUsername + ":" + adminPassword;
        String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        String extraHeader = "Basic " + base64Credentials;

        LOGGER.info("Creating repository from " + uri + " to " + adminLocalPath.getAbsolutePath());

        try (Git result = Git.cloneRepository()
                .setURI(uri)
                .setDirectory(adminLocalPath)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(adminUsername, adminPassword))
                .setTransportConfigCallback(transport -> {
                    if (transport instanceof TransportHttp) {
                        ((TransportHttp) transport)
                                .setAdditionalHeaders(Collections.singletonMap("Authorization", extraHeader));
                    }
                })
                .call()) {
            LOGGER.info("Repository created successfully");
            assert result.getRepository().getDirectory().exists();
        } catch (GitAPIException e) {
            LOGGER.log(Level.SEVERE, "Failed to create repository: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error: " + e.getMessage(), e);
            throw e;
        }

        File anonymousLocalPath = new File("target/repos/admin-create-anonymous");
        anonymousLocalPath.getParentFile().mkdirs();

        LOGGER.info("Accessing repository anonymously from " + uri + " to " + anonymousLocalPath.getAbsolutePath());

        try (Git result = Git.cloneRepository()
                .setURI(uri)
                .setDirectory(anonymousLocalPath)
                .call()) {
            LOGGER.info("Repository accessed successfully");
            assert result.getRepository().getDirectory().exists();
        } catch (GitAPIException e) {
            LOGGER.log(Level.SEVERE, "Failed to access repository anonymously: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error: " + e.getMessage(), e);
            throw e;
        }
    }
}
