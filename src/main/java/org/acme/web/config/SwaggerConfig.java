package org.acme.web.config;

import java.util.Map;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.XML;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library API")
                        .version("1.0.0")
                        .description("A basic library management API with books and authors"));
    }

    /**
     * Set XML root name to "message" for CreateMessageRequest so Swagger UI shows
     * &lt;message&gt; in XML examples.
     */
    @Bean
    @SuppressWarnings("rawtypes")
    public OpenApiCustomizer messageRequestXmlNameCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null || openApi.getComponents().getSchemas() == null) {
                return;
            }
            Map<String, Schema> schemas = openApi.getComponents().getSchemas();
            Schema createMessageRequest = schemas.get("CreateMessageRequest");
            if (createMessageRequest != null) {
                createMessageRequest.setXml(new XML().name("message"));
            }
        };
    }
}
