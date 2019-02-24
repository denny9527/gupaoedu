package com.denny.microservice.person.feign.client.ribbon;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomConfig {

    @Bean
    public CustomRule custRule() {
       return new CustomRule();
    }
}
