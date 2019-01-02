package com.denny.dubbo;


import com.alibaba.dubbo.container.Main;

/**
 * dubbo 容器机制。容器类型：
 * Spring
 * Log4j
 * Logback
 * Jetty
 */
public class ContainerMain {


    public static void main(String[] args){

        Main.main(new String[]{"spring", "log4j"});

    }
}
