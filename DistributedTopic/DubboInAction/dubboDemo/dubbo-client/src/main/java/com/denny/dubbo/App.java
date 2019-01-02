package com.denny.dubbo;

import com.alibaba.dubbo.common.extension.ExtensionFactory;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.rpc.Protocol;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("dubbo-client.xml");
        //IZkHello zkHelloService = (IZkHello) appContext.getBean("zkHelloService");
        //System.out.println(zkHelloService.hello("ZhangKui"));

        //dubbo 服务集群负载演示

        //for(int i = 0; i < 10; i++){
        //    IZkHello zkHelloService = (IZkHello) appContext.getBean("zkHelloService");
        //    System.out.println(zkHelloService.hello("ZhangKui"));
        //
        //    Thread.sleep(2000);
        //}

        //服务多版本支持 version = 1.0.1 演示服务降级Mock
        IZkHello zkHelloService = (IZkHello) appContext.getBean("zkHelloService");
        System.out.println(zkHelloService.hello("ZhangKui"));

        //获取自适应扩展点
        System.out.println(ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getAdaptiveExtension());//AdaptiveExtensionFactory
        System.out.println(ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension());//Protocol$Adaptive 动态字节码生成的自适应适配器
        //获取扩展点
        System.out.println(ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getExtension("spi"));//SpiExtensionFactory

    }
}
