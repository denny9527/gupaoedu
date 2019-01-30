package com.denny.microservice.config.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableConfigServer
public class SpringCloudConfigServer {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigServer.class, args);
    }

    //自定义EnvironmentRepository
//    @Bean
//    public EnvironmentRepository environmentRepository(){
//        return (application, profile, label) -> {
//            Environment environment = new Environment(profile, "default");
//            Map<String, String> map = new HashMap<String, String>();
//            map.put("name", "张奎");
//            PropertySource propertySource = new PropertySource("map", map);
//            environment.add(propertySource);
//            return environment;
//        };
//    }

}
