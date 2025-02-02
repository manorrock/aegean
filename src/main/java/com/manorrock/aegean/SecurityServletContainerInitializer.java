package com.manorrock.aegean;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The Security ServletContainerInitializer.
 * 
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class SecurityServletContainerInitializer implements ServletContainerInitializer {

    /**
     * Stores the logger.
     */
    private static final Logger LOGGER = Logger.getLogger(SecurityServletContainerInitializer.class.getName());

    /**
     * Called when the application is starting up.
     * <p>
     * This method sets the context parameters for admin username, admin password,
     * and anonymous access disabled based on environment variables or system
     * properties when found.
     * 
     * @param classes        the set of classes
     * @param servletContext the servlet context
     * @throws ServletException when a servlet error occurs
     */
    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
        String adminUsername = System.getenv("AEGEAN_ADMIN_USERNAME");
        if (adminUsername != null) {
            LOGGER.info("Admin username obtained from environment variable AEGEAN_ADMIN_USERNAME");
        } else {
            adminUsername = System.getProperty("com.manorrock.aegean.adminUsername");
            if (adminUsername != null) {
                LOGGER.info("Admin username obtained from system property com.manorrock.aegean.adminUsername");
            }
        }

        if (adminUsername != null) {
            servletContext.setInitParameter("adminUsername", adminUsername);
        }

        String adminPassword = System.getenv("AEGEAN_ADMIN_PASSWORD");
        if (adminPassword != null) {
            LOGGER.info("Admin password obtained from environment variable AEGEAN_ADMIN_PASSWORD");
        } else {
            adminPassword = System.getProperty("com.manorrock.aegean.adminPassword");
            if (adminPassword != null) {
                LOGGER.info("Admin password obtained from system property com.manorrock.aegean.adminPassword");
            }
        }

        if (adminPassword != null) {
            servletContext.setInitParameter("adminPassword", adminPassword);
        }

        String anonymousDisabled = System.getenv("AEGEAN_ANONYMOUS_DISABLED");
        if (anonymousDisabled != null) {
            LOGGER.info("Anonymous access disabled obtained from environment variable AEGEAN_ANONYMOUS_DISABLED");
        } else {
            anonymousDisabled = System.getProperty("com.manorrock.aegean.anonymousDisabled");
            if (anonymousDisabled != null) {
                LOGGER.info(
                        "Anonymous access disabled obtained from system property com.manorrock.aegean.anonymousDisabled");
            }
        }

        if (anonymousDisabled != null) {
            servletContext.setInitParameter("anonymousDisabled", anonymousDisabled);
        }
    }
}
