package com.manorrock.aegean;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The Admin Servlet Container Initializer.
 * 
 * This initializer sets the admin username and password context parameters
 * based on the environment variables or system properties AEGEAN_ADMIN_USERNAME and AEGEAN_ADMIN_PASSWORD.
 * 
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class SecurityServletContainerInitializer implements ServletContainerInitializer {

    /**
     * Stores the logger.
     */
    private static final Logger LOGGER = Logger.getLogger(SecurityServletContainerInitializer.class.getName());

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
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
            ctx.setInitParameter("adminUsername", adminUsername);
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
            ctx.setInitParameter("adminPassword", adminPassword);
        }

        String anonymousDisabled = System.getenv("AEGEAN_ANONYMOUS_DISABLED");
        if (anonymousDisabled != null) {
            LOGGER.info("Anonymous access disabled obtained from environment variable AEGEAN_ANONYMOUS_DISABLED");
        } else {
            anonymousDisabled = System.getProperty("com.manorrock.aegean.anonymousDisabled");
            if (anonymousDisabled != null) {
                LOGGER.info("Anonymous access disabled obtained from system property com.manorrock.aegean.anonymousDisabled");
            }
        }

        if (anonymousDisabled != null) {
            ctx.setInitParameter("anonymousDisabled", anonymousDisabled);
        }
    }
}
