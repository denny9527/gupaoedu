package com.denny.rabbitmq;

import com.denny.rabbitmq.producer.TestProducer;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RabbitTest {


    @Test
    public void sendMessage() throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        TestProducer testProducer = (TestProducer)applicationContext.getBean("testProducer");

        int loop = 0;

        while(loop < 100) {
            System.out.println("第"+loop+"条信息发送！");
            testProducer.sendMsg(new String("Hello world!"+ loop));
            Thread.sleep(1000);
            loop++;
        }

    }

}
