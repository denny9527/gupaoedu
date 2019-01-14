package com.denny.rabbitmq.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestProducer {

    private Logger logger = LoggerFactory.getLogger(TestProducer.class);

    @Autowired
    private AmqpTemplate amqpTemplate;


    public void sendMsg(Object message){
        logger.info("发送消息内容为："+"[DIRECT]"+message);
        amqpTemplate.convertAndSend("denny.test","[DIRECT]"+message);
    }

}
