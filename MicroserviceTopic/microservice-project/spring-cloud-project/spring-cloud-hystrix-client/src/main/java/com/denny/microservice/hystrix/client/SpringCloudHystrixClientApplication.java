package com.denny.microservice.hystrix.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication(scanBasePackages = {"com.denny.microservice.hystrix"})
//@EnableHystrix
//@EnableHystrixDashboard
@EnableCircuitBreaker //启动熔断保护
public class SpringCloudHystrixClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudHystrixClientApplication.class, args);
    }
}
