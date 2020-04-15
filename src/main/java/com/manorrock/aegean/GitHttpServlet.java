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
import java.util.Enumeration;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.INFO;
import java.util.logging.Logger;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jgit.http.server.GitFilter;

/**
 * The Git HTTP servlet.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@WebServlet(name = "GitHttpServlet", urlPatterns = {"/*"})
public class GitHttpServlet extends HttpServlet {

    /**
     * Stores the logger.
     */
    private static final Logger LOGGER = Logger.getLogger(GitHttpServlet.class.getName());

    /**
     * Stores the Git filter.
     */
    private transient GitFilter filter;

    /**
     * Stores the repository resolver.
     */
    private transient GitRepositoryResolver repositoryResolver;

    /**
     * Stores the root directory.
     */
    private File rootDirectory;

    /**
     * Stores the root directory filename.
     */
    private String rootDirectoryFilename;

    /**
     * Destroy the servlet.
     */
    @Override
    public void destroy() {
        if (LOGGER.isLoggable(FINEST)) {
            LOGGER.entering(GitHttpServlet.class.getName(), "destroy");
        }
        filter.destroy();
        if (LOGGER.isLoggable(FINEST)) {
            LOGGER.exiting(GitHttpServlet.class.getName(), "destroy");
        }
    }

    /**
     * Initialize the servlet.
     *
     * @param config the servlet config.
     * @throws ServletException when a Servlet error occurs.
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        if (LOGGER.isLoggable(FINEST)) {
            LOGGER.entering(GitHttpServlet.class.getName(), "init");
        }
        
        rootDirectoryFilename = System.getenv("REPOSITORIES_DIRECTORY");
        if (rootDirectoryFilename == null) {
            rootDirectoryFilename = System.getProperty("REPOSITORIES_DIRECTORY",
                    System.getProperty("user.home") + "/.manorrock/aegean/repositories");
        }

        if (LOGGER.isLoggable(INFO)) {
            LOGGER.log(INFO, "Repositories directory: {0}", rootDirectoryFilename);
        }
        
        rootDirectory = new File(rootDirectoryFilename);

        if (!rootDirectory.exists()) {
            rootDirectory.mkdirs();
        }

        if (repositoryResolver == null) {
            repositoryResolver = new GitRepositoryResolver(rootDirectory);
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
        
        if (LOGGER.isLoggable(FINEST)) {
            LOGGER.exiting(GitHttpServlet.class.getName(), "init");
        }
    }

    /**
     * Process the request.
     *
     * @param request the request.
     * @param response the response.
     * @throws ServletException when a Servlet error occurs.
     * @throws IOException when an I/O error occurs.
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (LOGGER.isLoggable(FINEST)) {
            LOGGER.entering(GitHttpServlet.class.getName(), "service");
        }
        filter.doFilter(request, response, (ServletRequest servletRequest, ServletResponse servletResponse) -> {
            if (servletRequest instanceof HttpServletRequest) {
                HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        });
        if (LOGGER.isLoggable(FINEST)) {
            LOGGER.exiting(GitHttpServlet.class.getName(), "service");
        }
    }
}
