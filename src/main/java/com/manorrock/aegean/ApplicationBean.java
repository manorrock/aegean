/*
 *  Copyright (c) 2002-2020, Manorrock.com. All Rights Reserved.
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
import static java.util.logging.Level.INFO;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * The one and only application bean.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@ApplicationScoped
public class ApplicationBean {
    
    /**
     * Stores the logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ApplicationBean.class.getName());

    /**
     * Stores the repositories directory.
     */
    private File repositoriesDirectory;

    /**
     * Initialize.
     */
    @PostConstruct
    public void initialize() {
        String rootDirectoryFilename = System.getenv("REPOSITORIES_DIRECTORY");
        if (rootDirectoryFilename == null) {
            rootDirectoryFilename = System.getProperty("REPOSITORIES_DIRECTORY",
                    System.getProperty("user.home") + "/.manorrock/aegean/repositories");
        }

        if (LOGGER.isLoggable(INFO)) {
            LOGGER.log(INFO, "Repositories directory: {0}", rootDirectoryFilename);
        }
        
        repositoriesDirectory = new File(rootDirectoryFilename);

        if (!repositoriesDirectory.exists()) {
            repositoriesDirectory.mkdirs();
        }
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
