package com.denny.dubbo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("dubbo-client.xml");
        IZkHello zkHelloService = (IZkHello) appContext.getBean("zkHelloService");
        System.out.println(zkHelloService.hello("ZhangKui"));

    }
}
