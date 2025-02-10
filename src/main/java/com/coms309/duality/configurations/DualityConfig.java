package com.coms309.duality.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DualityConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
