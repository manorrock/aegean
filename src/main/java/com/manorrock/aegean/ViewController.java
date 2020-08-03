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
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import static java.util.logging.Level.WARNING;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.omnifaces.oyena.action.ActionMapping;

/**
 * The controller for viewing a repository.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@Named("viewController")
@RequestScoped
public class ViewController implements Serializable {

    /**
     * Stores the logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ViewController.class.getName());

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
     * Stores the current directory.
     */
    private String currentDirectory = "";

    /**
     * Stores the files.
     */
    private final ArrayList<FileModel> files = new ArrayList<>();

    /**
     * Stores the repository.
     */
    private String repository;

    /**
     * Get the files.
     *
     * @return the files.
     */
    public List<FileModel> getFiles() {
        return files;
    }

    /**
     * Get the repository.
     *
     * @return the repository.
     */
    public String getRepository() {
        return repository;
    }

    /**
     * Get the clone URL.
     *
     * @return the clone URL.
     */
    public String getCloneUrl() {
        return cloneUrl;
    }

    /**
     * Execute the page.
     *
     * @param request the HTTP servlet request.
     * @return /index.xhtml
     */
    @ActionMapping("/view/*")
    public String view(HttpServletRequest request) {
        String refParameter = request.getParameter("ref");
        if (refParameter == null) {
            refParameter = "master";
        }

        repository = request.getRequestURI().substring(
                request.getRequestURI().lastIndexOf("/") + 1);

        try ( Repository fileRepository = new FileRepositoryBuilder()
                .setGitDir(new File(application.getRepositoriesDirectory(), repository + ".git"))
                .findGitDir()
                .build()) {

            Ref ref = fileRepository.getRefDatabase().findRef(refParameter);
            RevWalk revWalk = new RevWalk(fileRepository);

            if (ref != null) {
                RevCommit commit = revWalk.parseCommit(ref.getObjectId());
                RevTree tree = commit.getTree();

                try ( TreeWalk treeWalk = new TreeWalk(fileRepository)) {
                    treeWalk.addTree(tree);
                    if (currentDirectory.equals("")) {
                        treeWalk.setRecursive(false);
                    } else {
                        treeWalk.setRecursive(false);
                        treeWalk.setFilter(PathFilterGroup.createFromStrings(currentDirectory));
                    }
                    while (treeWalk.next()) {
                        String filename = treeWalk.getNameString();
                        if (treeWalk.isSubtree()) {
                            FileModel fileModel = new FileModel();
                            fileModel.setFilename(filename);
                            fileModel.setDirectory(true);
                            files.add(fileModel);
                        } else {
                            FileModel fileModel = new FileModel();
                            fileModel.setFilename(treeWalk.getNameString());
                            files.add(fileModel);
                        }
                    }
                }
            }

            if (request.getScheme().equalsIgnoreCase("https")) {
                cloneUrl = request.getScheme() + "://" + request.getServerName()
                        + (request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) 
                        + (request.getContextPath().equals("") ? "/" : request.getContextPath()) 
                        + "repositories/" + repository + ".git";
            } else {
                cloneUrl = request.getScheme() + "://" + request.getServerName()
                        + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) 
                        + (request.getContextPath().equals("") ? "/" : request.getContextPath()) 
                        + "repositories/" + repository + ".git";
            }
        } catch (IOException ioe) {
            if (LOGGER.isLoggable(WARNING)) {
                LOGGER.log(WARNING, "Error viewing repository", ioe);
            }
        }
        return "/WEB-INF/ui/view.xhtml";
    }
}
