package com.denny.microservice.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAutoConfiguration
@EnableWebMvc
@ComponentScan(basePackages = {"com.denny.microservice.mvc"})
public class MvcRestApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MvcRestApplication.class, args);
        System.out.println(context.getBean(MvcRestApplication.class));

        //new SpringApplication(MvcRestApplication.class).run(args);
    }


}
