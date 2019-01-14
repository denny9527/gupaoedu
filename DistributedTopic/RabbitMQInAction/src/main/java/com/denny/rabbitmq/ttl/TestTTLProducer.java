package com.denny.rabbitmq.ttl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 如果队列设置了全局TTL和消息设置了TTL，以队列的TTL设置为准。
 */
public class TestTTLProducer {

    public static void main(String[] args) throws IOException, TimeoutException {
        //连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //连接HOST
        connectionFactory.setHost("127.0.0.1");
        //连接端口号
        connectionFactory.setPort(5672);
        //虚拟机
        connectionFactory.setVirtualHost("/");
        //账号
        connectionFactory.setUsername("guest");
        //密码
        connectionFactory.setPassword("guest");

        //连接
        Connection connection = connectionFactory.newConnection();

        //消息通道
        Channel channel = connection.createChannel();

        //通过队列属性设置消息过期时间
        Map<String, Object> basicProperties = new LinkedHashMap<String, Object>();
        basicProperties.put("x-message-ttl", 6000);

        //使用RabbitMQ默认交换机
        channel.queueDeclare("TEST_TTL_QUEUE", false, false, false, basicProperties);

        //对每条消息设置过期时间
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2) //持久化消息
                .contentEncoding("UTF-8")
                .expiration("20000") //TTL
                .build();


        //消息内容
        String msg = "hello world RabbitMQ! Msg TTL";

        channel.basicPublish("", "TEST_TTL_QUEUE", properties, msg.getBytes());


        channel.close();
        connection.close();
    }

}
