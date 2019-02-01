package com.denny.microservice.person.feign.service.provider;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
public class SpringCloudFeignServiceProviderApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringCloudFeignServiceProviderApplication.class)
                .run(args);
    }

}
