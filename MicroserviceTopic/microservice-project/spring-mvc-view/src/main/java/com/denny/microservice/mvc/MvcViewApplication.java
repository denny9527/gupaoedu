package com.denny.microservice.mvc;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
public class MvcViewApplication {

    public static void main(String[] args) {
        //1、使用SpringApplication 引导
        SpringApplication.run(MvcViewApplication.class, args);
    }

}

