package com.denny.microservice.cloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
//@ComponentScan
@RestController
public class SpringBootApplicationBootstrap {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setId("zhangkui");
        context.registerBean("helloWorld", String.class, "helloWorld");
        context.refresh();

        //类比Spring WebMVC、Root WebApplication 和 DispatcherServlet WebApplication
        //DispatcherServlet WebApplication parent = Root WebApplication

        new SpringApplicationBuilder(SpringBootApplicationBootstrap.class)
                .parent(context)
                .run(args);

//        SpringApplication.run(SpringBootApplicationBootstrap.class, args);
    }

    @Autowired
    @Qualifier("helloWorld")
    private String message;

    @RequestMapping("/index")
    public String index(){
        return message;
    }

}
