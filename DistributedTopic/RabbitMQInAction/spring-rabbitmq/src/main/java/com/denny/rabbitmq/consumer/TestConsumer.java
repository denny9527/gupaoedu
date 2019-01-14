package com.denny.rabbitmq.consumer;

import com.denny.rabbitmq.producer.TestProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 实现 ChannelAwareMessageListener 接口，重写 onMessage(Message var1, Channel var2) 方法用于手动确认消息如：
 * channel.basicAck、channel.basicNack、channel.basicReject
 */
public class TestConsumer implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(TestConsumer.class);

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public void onMessage(Message message) {
        logger.info("接受到消息内容为："+message);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
