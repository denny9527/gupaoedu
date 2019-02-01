package com.denny.microservice.person.feign.client;

import com.denny.microservice.spring.cloud.feign.api.service.PersonService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = {PersonService.class})
@EnableEurekaClient
public class SpringCloudFeignClientApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringCloudFeignClientApplication.class)
                .run(args);
    }

}
