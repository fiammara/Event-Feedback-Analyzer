package com.rasa.eventsync.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CorsConfig {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);
    private final Environment env;

    public CorsConfig(Environment env) {
        this.env = env;
           log.info("CorsConfig bean is being instantiated");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        String allowedOriginsProperty = env.getProperty("cors.allowed-origins");
        log.info("CORS allowed-origins property: '{}'", allowedOriginsProperty);

        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = new ArrayList<>();

        if (allowedOriginsProperty != null && !allowedOriginsProperty.isEmpty()) {
            // Split and trim all origins
            String[] originsArray = allowedOriginsProperty.split(",");
            for (String origin : originsArray) {
                String trimmed = origin.trim();
                origins.add(trimmed);
                log.info("Added CORS origin: '{}'", trimmed);
            }
        } else {
            // Default origins for local dev
            origins.add("http://localhost:5173");
            origins.add("http://localhost:3000");
            log.info("Using default CORS origins");
        }

        // Set origins (handles exact matches)
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        config.setAllowedHeaders(java.util.Arrays.asList("*"));
        config.setExposedHeaders(java.util.Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        log.info("CORS configuration complete with {} allowed origins", origins.size());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // Register Spring's CorsFilter with highest precedence to handle preflight early
    @Bean
    public FilterRegistrationBean<org.springframework.web.filter.CorsFilter> corsFilterRegistration(
            CorsConfigurationSource corsConfigurationSource) {
        FilterRegistrationBean<org.springframework.web.filter.CorsFilter> bean =
                new FilterRegistrationBean<>(new org.springframework.web.filter.CorsFilter(corsConfigurationSource));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        log.info("Registered CorsFilter with HIGHEST_PRECEDENCE");
        return bean;
    }
}

