/*
 *  Copyright (c) 2002-2023, Manorrock.com. All Rights Reserved.
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
import java.nio.file.Files;
import java.nio.file.Path;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import java.util.logging.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * The one and only application bean.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@ApplicationScoped
public class Application {

    /**
     * Stores the logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

    /**
     * Stores the repositories directory.
     */
    private File repositoriesDirectory;

    /**
     * Initialize.
     */
    @PostConstruct
    public void initialize() {
        String rootDirectoryFilename = System.getenv("ROOT_DIRECTORY");
        if (rootDirectoryFilename == null) {
            rootDirectoryFilename = System.getProperty("ROOT_DIRECTORY",
                    System.getProperty("user.home") + "/.manorrock/aegean");
        }

        if (LOGGER.isLoggable(INFO)) {
            LOGGER.log(INFO, "Base directory: {0}", rootDirectoryFilename);
        }

        repositoriesDirectory = new File(rootDirectoryFilename, "repositories");

        if (!repositoriesDirectory.exists()) {
            repositoriesDirectory.mkdirs();
        }
    }

    /**
     * Get the repository.
     *
     * @param name the name.
     * @return the repository.
     */
    public Repository getRepository(String name) {
        Repository repository = new Repository();
        repository.setName(name);
        repository.setSize(getRepositorySize(name));
        return repository;
    }
    
    /**
     * Get the repository size.
     * 
     * @param name the name of the repository.
     * @return the size.
     */
    public long getRepositorySize(String name) {
        Path directory = repositoriesDirectory.toPath().resolve(name);
        long size = -1;
        try {
            size = Files.walk(directory)
                    .filter(p -> p.toFile().isFile())
                    .mapToLong(p -> p.toFile().length())
                    .sum();
        } catch (IOException ioe) {
            LOGGER.log(WARNING, "Unable to determine size of repository", ioe);
        }
        return size;
    }

    /**
     * Get the repositories directory.
     *
     * @return the repositories directory.
     */
    public File getRepositoriesDirectory() {
        return repositoriesDirectory;
    }

    /**
     * Set the repositories directory.
     *
     * @param repositoriesDirectory the repositories directory.
     */
    public void setRepositoriesDirectory(File repositoriesDirectory) {
        this.repositoriesDirectory = repositoriesDirectory;
    }
}
