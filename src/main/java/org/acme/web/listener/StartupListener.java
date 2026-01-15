package org.acme.web.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");

        String baseUrl = "http://localhost:" + port + contextPath;
        String swaggerUrl = baseUrl + "/swagger-ui.html";

        log.info("===========================================");
        log.info("Library API is running!");
        log.info("Swagger UI: {}", swaggerUrl);
        log.info("API Docs: {}", baseUrl + "/v3/api-docs");
        log.info("===========================================");
    }
}
