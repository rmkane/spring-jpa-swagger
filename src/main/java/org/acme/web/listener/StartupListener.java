package org.acme.web.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(StartupListener.class);

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");

        String baseUrl = "http://localhost:" + port + contextPath;
        String swaggerUrl = baseUrl + "/swagger-ui.html";

        logger.info("===========================================");
        logger.info("Library API is running!");
        logger.info("Swagger UI: {}", swaggerUrl);
        logger.info("API Docs: {}", baseUrl + "/v3/api-docs");
        logger.info("===========================================");
    }
}
