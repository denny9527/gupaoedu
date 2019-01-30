package com.denny.microservice;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;


public class SpringBootEventDemo {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringBootEventDemo.class)
                .listeners(new ApplicationListener<ApplicationEvent>() {
                    @Override
                    public void onApplicationEvent(ApplicationEvent applicationEvent) {
                        System.out.println("监听事件：" + applicationEvent);
                    }
                })
              .run(args).close();
          /*
            监听到的事件，如下：
            ApplicationStartingEvent
            ApplicationEnvironmentPreparedEvent
            ApplicationContextInitializedEvent
            ApplicationPreparedEvent
            ContextRefreshedEvent
            ServletWebServerInitializedEvent
            ApplicationStartedEvent
            ApplicationReadyEvent
            ContextClosedEvent
           */
    }
}
