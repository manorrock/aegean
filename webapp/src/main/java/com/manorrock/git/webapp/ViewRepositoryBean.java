/*
 *  Copyright (c) 2002-2017, Manorrock.com. All Rights Reserved.
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
package com.manorrock.git.webapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

/**
 * The bean for viewing the repository.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@ManagedBean
@RequestScoped
public class ViewRepositoryBean {

    /**
     * Stores the logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ViewRepositoryBean.class.getName());

    /**
     * Stores the branch.
     */
    private String branch;

    /**
     * Stores the branches.
     */
    private SelectItem[] branches;

    /**
     * Stores the Git repository url.
     */
    private String gitReposUrl;

    /**
     * Stores the repository name.
     */
    private String repositoryName;

    /**
     * Stores the checkout URL.
     */
    private String checkoutUrl;

    /**
     * Get the branch.
     *
     * @return the branch.
     */
    public String getBranch() {
        return branch;
    }

    /**
     * Get the branches.
     *
     * @return the branches.
     */
    public SelectItem[] getBranches() {
        return branches;
    }

    /**
     * Get the checkout URL.
     *
     * @return the checkout URL.
     */
    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    /**
     * Initialize the bean.
     */
    @PostConstruct
    public void initialize() {
        if (gitReposUrl == null) {
            try {
                InitialContext initialContext = new InitialContext();
                gitReposUrl = (String) initialContext.lookup("java:comp/env/gitReposUrl");
            } catch (NamingException ne) {
                gitReposUrl = null;
            }
        }

        if (gitReposUrl == null || "".equals(gitReposUrl.trim())) {
            gitReposUrl = System.getenv("GIT_REPOS_URL");
        }

        if (gitReposUrl == null || "".equals(gitReposUrl.trim())) {
            gitReposUrl = System.getProperty("GIT_REPOS_URL", "http://localhost:8080/git/repos");
        }

        repositoryName = FacesContext.getCurrentInstance().getExternalContext().
                getRequestParameterMap().get("repositoryName");

        try {
            ArrayList<SelectItem> selectItems = new ArrayList<>();
            Collection<Ref> refs = Git.lsRemoteRepository()
                    .setHeads(true)
                    .setRemote(gitReposUrl + "/" + repositoryName)
                    .call();

            refs.forEach((ref) -> {
                selectItems.add(new SelectItem(ref.getName()));
            });

            branches = selectItems.toArray(new SelectItem[0]);
        } catch (GitAPIException gae) {
            LOGGER.log(Level.SEVERE, "Unable to get list of branches", gae);
        }
        branch = "master";
        checkoutUrl = gitReposUrl + "/" + repositoryName;
    }
}
