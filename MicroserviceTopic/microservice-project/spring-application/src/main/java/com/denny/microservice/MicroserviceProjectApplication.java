package com.denny.microservice;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
public class MicroserviceProjectApplication {



    public static void main(String[] args) {
        //1、使用SpringApplication 引导
        //SpringApplication.run(MicroserviceProjectApplication.class, args);

//        SpringApplication springApplication = new SpringApplication(MicroserviceProjectApplication.class);
//        Map<String, Object> properties = new LinkedHashMap<String, Object>();
//        properties.put("server.port", 0);
//        springApplication.setDefaultProperties(properties);
//        ConfigurableApplicationContext context = springApplication.run(args);

//        System.out.println(context.getBean(MicroserviceProjectApplication.class));

        //2、SpringApplicationBuilder 引导
//        new SpringApplicationBuilder(MicroserviceProjectApplication.class)// Fluent API
//                //单元测试是 PORT=随机端口
//                .properties("server.port=0") //随机向OS要可用端口
//                .run(args);

        //设置为非Web应用
        SpringApplication springApplication = new SpringApplication(MicroserviceProjectApplication.class);
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put("server.port", 0);
        springApplication.setDefaultProperties(properties);
        //设置为非Web应用
        springApplication.setBannerMode(Banner.Mode.OFF);//关闭启动标语
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext context = springApplication.run(args);

        //输出当前Spring Boot应用的ApplicationContext的类名
        System.out.println("当前Spring应用上下文的类"+context.getClass().getName());
    }

}

