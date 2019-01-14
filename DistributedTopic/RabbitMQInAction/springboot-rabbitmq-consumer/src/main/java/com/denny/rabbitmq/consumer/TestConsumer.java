package com.denny.rabbitmq.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "TEST_QUEUE")
public class TestConsumer {

    @RabbitHandler
    public void process(String message){
        System.out.println("接受到的消息内容为："+message);
    }
}
