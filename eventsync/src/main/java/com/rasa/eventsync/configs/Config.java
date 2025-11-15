package com.rasa.eventsync.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Event API")
                .version("1.0")
                .description("Event Sync API"));
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();

    }

}