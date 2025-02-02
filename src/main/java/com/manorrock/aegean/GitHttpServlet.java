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
 * The Git HttpServlet.
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
    private GitApplication application;

    /**
     * Stores the Git filter.
     */
    private transient GitFilter filter;

    /**
     * Stores the repository resolver.
     */
    private transient GitRepositoryResolver repositoryResolver;

    /**
     * Stores the maximum upload size (defaults to 512 MB).
     */
    private long maxUploadSize = 512L * 1024 * 1024;

    /**
     * Destroy the servlet.
     */
    @Override
    public void destroy() {
        filter.destroy();
    }

    /**
     * Initialize the servlet.
     *
     * This method is called once when the servlet is first loaded into memory.
     * It initializes the Git filter and sets up the repository resolver and
     * upload size limit based on the servlet configuration.
     *
     * @param config the ServletConfig object that contains
     *               configuration information for this servlet.
     * @throws ServletException if an exception occurs that interrupts
     *                          the servlet's normal operation.
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        
        String maxUploadSizeParam = config.getInitParameter("maxUploadSize");
        if (maxUploadSizeParam != null) {
            try {
                maxUploadSize = Long.parseLong(maxUploadSizeParam);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid maxUploadSize parameter, using 512 MB as default value");
            }
        }

        if (repositoryResolver == null) {
            repositoryResolver = new GitRepositoryResolver(application.getRepositoriesDirectory());
        }

        filter = new GitFilter();
        filter.setRepositoryResolver(repositoryResolver);
        filter.addUploadPackFilter((request, response, chain) -> {
            if (request.getContentLengthLong() > maxUploadSize && maxUploadSize > 0) {
                ((HttpServletResponse) response).sendError(
                    HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, 
                "Upload size exceeds the maximum allowed size which is " + maxUploadSize + " bytes.");
            } else {
                chain.doFilter(request, response);
            }
        });

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
    }

    /**
     * Process an HTTP request.
     *
     * This method is called for each HTTP request to the servlet. It uses the
     * Git filter to handle the request. If the request is not an HTTP-based
     * request that the Git filter can process, it sends a 404 error indicating
     * that the requested resource is not found.
     *
     * @param request  the HttpServletRequest object that contains
     *                 the request the client has made of the servlet.
     * @param response the HttpServletResponse object that contains
     *                 the response the servlet sends to the client.
     * @throws ServletException if an exception occurs that interferes
     *                          with the servlet's normal operation.
     * @throws IOException      if an input or output error is detected
     *                          when the servlet handles the request.
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        filter.doFilter(request, response, (ServletRequest servletRequest, ServletResponse servletResponse) -> {
            if (servletRequest instanceof HttpServletRequest) {
                HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        });
    }
}
