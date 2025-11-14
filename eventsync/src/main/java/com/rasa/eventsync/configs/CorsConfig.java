package com.rasa.eventsync.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);
    private final Environment env;

    public CorsConfig(Environment env) {
        this.env = env;
        log.info("CorsConfig bean is being instantiated");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String allowedOriginsProperty = env.getProperty("cors.allowed-origins");
        log.info("CORS allowed-origins property: '{}'", allowedOriginsProperty);

        List<String> origins = new ArrayList<>();

        if (allowedOriginsProperty != null && !allowedOriginsProperty.isEmpty()) {

            String[] originsArray = allowedOriginsProperty.split(",");
            for (String origin : originsArray) {
                String trimmed = origin.trim();
                origins.add(trimmed);
                log.info("Added CORS origin: '{}'", trimmed);
            }
        } else {

            origins.add("http://localhost:5173");
            origins.add("http://localhost:3000");
            log.info("Using default CORS origins");
        }


        String[] originsArray = origins.toArray(new String[0]);

        registry.addMapping("/**")
                .allowedOrigins(originsArray)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

        log.info("CORS configuration complete with {} allowed origins", origins.size());
    }
}

