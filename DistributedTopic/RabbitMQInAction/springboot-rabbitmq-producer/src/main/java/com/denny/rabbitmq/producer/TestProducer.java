package com.denny.rabbitmq.producer;

import com.denny.rabbitmq.SpringbootRabbitmqProducerApplication;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMsg(){
        rabbitTemplate.convertAndSend("TEST_DIRECT_EXCHANGE", "denny.test", "Hello world!");
    }
}
