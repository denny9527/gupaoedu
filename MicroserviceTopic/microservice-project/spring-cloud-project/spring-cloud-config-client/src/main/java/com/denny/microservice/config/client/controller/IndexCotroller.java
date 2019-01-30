package com.denny.microservice.config.client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexCotroller {

    @GetMapping("/test_config")
    public String testConfig( @Value("${name}")String value){
        return value;
    }
}


