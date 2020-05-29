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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.oyena.action.ActionMapping;

/**
 * The controller for the index page.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@Named("indexController")
@RequestScoped
public class IndexController implements Serializable {

    /**
     * Stores the list of repositories.
     */
    private final List<String> repositories = new ArrayList();

    /**
     * Stores the application bean.
     */
    @Inject
    private Application application;

    /**
     * Initialize the bean.
     */
    @PostConstruct
    public void initialize() {
        File directory = application.getRepositoriesDirectory();
        repositories.addAll(Arrays.
                asList(directory.list()).
                stream().
                map(filename -> filename.substring(0, filename.indexOf(".git"))).
                collect(Collectors.toList()));
    }

    /**
     * Execute the page.
     *
     * @return /index.xhtml
     */
    @ActionMapping("/")
    public String execute() {
        return "/WEB-INF/ui/index.xhtml";
    }

    /**
     * Get the repositories.
     *
     * @return the repositories.
     */
    public List getRepositories() {
        return repositories;
    }
}
