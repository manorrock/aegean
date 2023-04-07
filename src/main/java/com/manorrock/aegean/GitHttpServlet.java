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

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;
import jakarta.inject.Inject;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jgit.http.server.GitFilter;

/**
 * The Git HTTP servlet.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class GitHttpServlet extends HttpServlet {

    /**
     * Stores the logger.
     */
    private static final Logger LOGGER = Logger.getLogger(GitHttpServlet.class.getName());

    /**
     * Stores the application.
     */
    @Inject
    private Application application;

    /**
     * Stores the Git filter.
     */
    private transient GitFilter filter;

    /**
     * Stores the repository resolver.
     */
    private transient GitRepositoryResolver repositoryResolver;

    /**
     * Destroy the servlet.
     */
    @Override
    public void destroy() {
        LOGGER.entering(GitHttpServlet.class.getName(), "destroy");
        filter.destroy();
        LOGGER.exiting(GitHttpServlet.class.getName(), "destroy");
    }

    @Override
    public void init(final ServletConfig config) throws ServletException {
        LOGGER.entering(GitHttpServlet.class.getName(), "init");

        if (repositoryResolver == null) {
            repositoryResolver = new GitRepositoryResolver(application.getRepositoriesDirectory());
        }

        filter = new GitFilter();
        filter.setRepositoryResolver(repositoryResolver);
        filter.init(new FilterConfig() {
            @Override
            public String getFilterName() {
                return filter.getClass().getName();
            }

            @Override
            public String getInitParameter(String name) {
                return config.getInitParameter(name);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return config.getInitParameterNames();
            }

            @Override
            public ServletContext getServletContext() {
                return config.getServletContext();
            }
        });

        LOGGER.exiting(GitHttpServlet.class.getName(), "init");
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LOGGER.entering(GitHttpServlet.class.getName(), "service");
        filter.doFilter(request, response, (ServletRequest servletRequest, ServletResponse servletResponse) -> {
            if (servletRequest instanceof HttpServletRequest) {
                HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        });
        LOGGER.exiting(GitHttpServlet.class.getName(), "service");
    }
}
