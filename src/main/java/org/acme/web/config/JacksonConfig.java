package org.acme.web.config;

import java.time.format.DateTimeFormatter;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Configuration
public class JacksonConfig {

    /**
     * Custom ObjectMapper for consistent LocalDateTime formatting.
     * 
     * Format: "yyyy-MM-dd HH:mm:ss" (e.g., "2025-12-09 20:35:44")
     * 
     * To use ISO-8601 format instead (default), remove this bean or change the
     * pattern. ISO-8601: "yyyy-MM-dd'T'HH:mm:ss" (e.g., "2025-12-09T20:35:44")
     * 
     * Note: Spring Boot already includes JavaTimeModule automatically, so we only
     * need to configure the serializer format.
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.serializers(
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
