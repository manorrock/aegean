/*
 *  Copyright (c) 2002-2021, Manorrock.com. All Rights Reserved.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.annotation.RequestParameterMap;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * The CDI bean for the /repository/view.xhtml page.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@Named(value = "repositoryViewPage")
@Dependent
public class RepositoryViewPage {

    /**
     * Stores the application.
     */
    @Inject
    private Application application;

    /**
     * Stores the clone URL.
     */
    private String cloneUrl;

    /**
     * Stores the commits.
     */
    private List<RevCommit> commits;

    /**
     * Stores the name.
     */
    @RequestParameterMap
    @Inject
    private Map parameterMap;

    /**
     * Stores the repository.
     */
    private Repository repository;

    /**
     * Stores the request.
     */
    @Inject
    private HttpServletRequest request;

    /**
     * Get the clone URL.
     *
     * @return the clone URL.
     */
    public String getCloneUrl() {
        return cloneUrl;
    }

    /**
     * Get the commits.
     *
     * @return the commits.
     */
    public List<RevCommit> getCommits() {
        return commits;
    }

    /**
     * Get the repository.
     *
     * @return the repository.
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * Initialize.
     */
    @PostConstruct
    public void initialize() {
        repository = application.getRepository((String) parameterMap.get("name"));
        commits = new ArrayList<>();
        try {
            org.eclipse.jgit.lib.Repository gitRepository = new FileRepositoryBuilder()
                    .setGitDir(new File(application.getRepositoriesDirectory(), repository.getName()))
                    .findGitDir()
                    .build();
            try ( RevWalk revWalk = new RevWalk(gitRepository)) {
                ObjectId gitObjectId = gitRepository.resolve("refs/heads/master");
                revWalk.markStart(revWalk.parseCommit(gitObjectId));
                for (RevCommit commit : revWalk) {
                    commits.add(commit);
                }
            }
        } catch (IOException ioe) {

        }
        cloneUrl = 
                request.getScheme() + "://" +
                request.getServerName() +
                (request.getServerPort() == 80 ? "" : 
                    (request.getServerPort() == 443 ? "" : ":" + request.getServerPort())) + 
                "/repositories/" + repository.getName();
    }
}
