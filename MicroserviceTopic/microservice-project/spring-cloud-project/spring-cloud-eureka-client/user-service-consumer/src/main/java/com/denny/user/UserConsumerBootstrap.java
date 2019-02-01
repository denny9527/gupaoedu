package com.denny.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
public class UserConsumerBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(UserConsumerBootstrap.class, args);

//        SpringApplication springApplication = new SpringApplication(UserConsumerBootstrap.class);
//        springApplication.setWebApplicationType(WebApplicationType.NONE);
//        springApplication.run();
    }

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

}
