package com.denny.spring;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.GenericApplicationContext;

public class SpringEventListenerDemo {

    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();

        /*只添加添加该监听器，泛型指定各种事件(即：ApplicationEvent) 此时会监听如下事件：
          ContextRefreshedEvent事件
          PayloadApplicationEvent事件
          MyEvent
          ContextClosedEvent
        */
//        context.addApplicationListener(new ApplicationListener<ApplicationEvent>() {
//            @Override
//            public void onApplicationEvent(ApplicationEvent applicationEvent) {
//                System.out.println("监听事件："+applicationEvent);
//            }
//        });

        context.addApplicationListener(new RefreshedListener());
        //只添加自定义上下文关闭事件监听器，只会监听 ContextClosedEvent 事件。
        context.addApplicationListener(new CloseListener());
        context.refresh();

        context.publishEvent("Hello World!");//实际上发布了PayloadApplicationEvent 事件
        //ContextRefreshedEvent事件
        //PayloadApplicationEvent事件
        context.publishEvent(new MyEvent("Hello World 2019"));

        //context.registerShutdownHook();//注册JVM关闭的钩子，调用了close()方法。

        //关闭应用上下文
        context.close();//ContextClosedEvent 事件

    }

    //自定义应用上下文事件监听器 泛型指定为某一事件类型
    private static class RefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

        @Override
        public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
            System.out.println("Spring 应用上下文启动！");
        }
    }

    //自定义应用上下文事件监听器 泛型指定为某一事件类型
    private static class CloseListener implements ApplicationListener<ContextClosedEvent> {
        @Override
        public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
            System.out.println("Spring 应用容器关闭！");
        }
    }

    private static class MyEvent extends ApplicationEvent {
        public MyEvent(Object source) {
            super(source);
        }
    }

}
