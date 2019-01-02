package com.denny.dubbo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class BootstrapCluster1 {

    public static void main( String[] args ) throws IOException {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("dubbo-cluster1.xml");
        appContext.start();
        System.in.read();

    }
}
