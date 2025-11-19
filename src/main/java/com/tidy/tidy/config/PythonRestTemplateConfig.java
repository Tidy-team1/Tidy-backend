package com.tidy.tidy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PythonRestTemplateConfig {
    @Bean
    public RestTemplate pythonRestTemplate() {
        return new RestTemplate();
    }
}
