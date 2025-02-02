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

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.resolver.FileResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;

/**
 * The Git repository resolver.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class GitRepositoryResolver extends FileResolver<HttpServletRequest> {

    /**
     * Stores the logger.
     */
    private static final Logger LOGGER = Logger.getLogger(GitRepositoryResolver.class.getName());

    /**
     * Stores the root directory.
     */
    private final File rootDirectory;

    /**
     * Constructor.
     *
     * Initializes the GitRepositoryResolver with the specified Git directory.
     * 
     * @param gitDirectory the Git directory.
     */
    public GitRepositoryResolver(File gitDirectory) {
        super(gitDirectory, true);
        this.rootDirectory = gitDirectory;
    }

    /**
     * Open the repository.
     * 
     * This method attempts to open an existing Git repository. If the repository
     * does not exist, it will create a new one if the user has the appropriate
     * permissions.
     * 
     * @param request the HTTP servlet request.
     * @param name    the repository name.
     * @return the repository.
     * @throws RepositoryNotFoundException if the repository is not found.
     * @throws ServiceNotEnabledException  if the service is not enabled.
     */
    @Override
    public Repository open(HttpServletRequest request, String name)
            throws RepositoryNotFoundException, ServiceNotEnabledException {

        Repository repository;
        try {
            String directoryName = name;
            if (directoryName.contains("/")) {
                directoryName = directoryName.substring(0, directoryName.indexOf('/'));
            }
            if (!directoryName.endsWith(".git")) {
                directoryName = directoryName + ".git";
            }
            File directory = new File(rootDirectory, directoryName);

            if (!directory.exists()) {
                String adminUsername = request.getServletContext().getInitParameter("adminUsername");

                if (adminUsername == null) {
                    LOGGER.info("Creating repository anonymously");
                    createRepository(directory, directoryName);
                } else if (request.getRemoteUser() != null && request.isUserInRole("admin")) {
                    LOGGER.info("Creating repository using admin role");
                    createRepository(directory, directoryName);
                } else {
                    throw new ServiceNotEnabledException("Only admin users can create new repositories.");
                }
            }

            repository = super.open(request, name);
        } catch (RepositoryNotFoundException | ServiceNotEnabledException e) {
            LOGGER.severe("Error opening repository: " + e.getMessage());
            throw e;
        }
        return repository;
    }

    /**
     * Create the repository.
     * 
     * This method creates a new Git repository in the specified directory.
     * 
     * @param directory     the repository directory.
     * @param directoryName the repository directory name.
     * @throws RepositoryNotFoundException if the repository cannot be created.
     */
    private void createRepository(File directory, String directoryName) throws RepositoryNotFoundException {
        try {
            Repository fileRepository = new FileRepositoryBuilder()
                    .setGitDir(directory)
                    .findGitDir()
                    .build();
            fileRepository.create(true);
            fileRepository.getConfig().setBoolean("http", null, "receivepack", true);
            fileRepository.getConfig().setBoolean("http", null, "uploadpack", true);
            fileRepository.getConfig().save();
            fileRepository.close();
        } catch (IOException ioe) {
            LOGGER.severe("Failed to create repository: " + directoryName);
            throw new RepositoryNotFoundException(directory, ioe);
        }
    }
}
