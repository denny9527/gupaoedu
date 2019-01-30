package com.denny.spring;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

public class ApplicationEventMulticasterDemo {

    public static void main(String[] args) {
        ApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();

        eventMulticaster.addApplicationListener(event -> {
            if (event instanceof PayloadApplicationEvent) {
                System.out.println("监听事件：" + PayloadApplicationEvent.class.cast(event) + "payload: " + PayloadApplicationEvent.class.cast(event).getPayload());
            }
            System.out.println("监听事件：" + event);
        });

        eventMulticaster.multicastEvent(new PayloadApplicationEvent<Object>("1", "Hello World"));
        eventMulticaster.multicastEvent(new PayloadApplicationEvent<Object>("2", "Hello World"));

    }
}
