package com.denny.spring;

import com.denny.microservice.MicroserviceProjectApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class SpringAnnotationDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(SpringAnnotationDemo.class);
        //上下文启动
        context.refresh();

        System.out.println(context.getBean(SpringAnnotationDemo.class));
    }
}
