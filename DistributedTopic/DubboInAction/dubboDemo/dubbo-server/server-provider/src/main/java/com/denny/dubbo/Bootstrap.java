package com.denny.dubbo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class Bootstrap {

    public static void main( String[] args ) throws IOException {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("dubbo-server.xml");
        appContext.start();
        System.in.read();

    }

}
