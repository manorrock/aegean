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
package com.manorrock.aegean.ui;

import com.manorrock.aegean.Repository;
import com.manorrock.aegean.Application;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import static java.util.logging.Level.WARNING;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * The CDI bean for the index.xhtml page.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@Named(value = "index")
@RequestScoped
public class IndexPage {
    
    /**
     * Stores the logger.
     */
    private static final Logger LOGGER = Logger.getLogger(IndexPage.class.getName());

    /**
     * Stores the application.
     */
    @Inject
    private Application application;
    
    /**
     * Stores the bytes converter.
     */
    private BytesConverter bytesConverter;

    /**
     * Stores the list of repositories.
     */
    private ArrayList<Repository> repositories;

    /**
     * Initialize.
     */
    @PostConstruct
    public void initialize() {
        bytesConverter = new BytesConverter();
        repositories = new ArrayList<>();
        String[] names = application.getRepositoriesDirectory().list();
        if (names != null) {
            for (String name : names) {
                Repository repository = new Repository();
                repository.setName(name);
                repository.setSize(determineSize(new File(application.getRepositoriesDirectory(), name)));
                repositories.add(repository);
            }
        }
    }

    /**
     * Get the bytes converter.
     * 
     * @return the bytes converter.
     */
    public BytesConverter getBytesConverter() {
        return bytesConverter;
    }

    /**
     * Get the list of repositories.
     *
     * @return the list of repositories.
     */
    public List<Repository> getRepositories() {
        return repositories;
    }

    /**
     * Determine the size of the given directory.
     *
     * @param directory the directory.
     * @return the size, or -1 if unable to determine.
     */
    private long determineSize(File directory) {
        long size = -1;
        try {
            size = Files.walk(directory.toPath())
                    .filter(p -> p.toFile().isFile())
                    .mapToLong(p -> p.toFile().length())
                    .sum();
        } catch (IOException ioe) {
            LOGGER.log(WARNING, "Unable to determine size of repository", ioe);
        }
        return size;
    }
}
