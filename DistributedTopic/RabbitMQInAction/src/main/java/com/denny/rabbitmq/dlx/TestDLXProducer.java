package com.denny.rabbitmq.dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 演示消息过期进入死信队列
 */
public class TestDLXProducer {

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
        basicProperties.put("x-message-ttl", 6000);//消息过期时间
        basicProperties.put("x-dead-letter-exchange", "DLX_EXCHANGE");//指定死信交换机
        basicProperties.put("x-max-priority", "10");//最大优先级

        //使用RabbitMQ默认交换机
        channel.queueDeclare("TEST_DLX_QUEUE", false, false, false, basicProperties);

        //声明死信交换机
        channel.exchangeDeclare("DLX_EXCHANGE", "topic", false, false, false, null);

        //声明死信队列
        channel.queueDeclare("DLX_QUEUE", false, false, false, null);

        //绑定死信交换机和死信队列
        channel.queueBind("DLX_QUEUE", "DLX_EXCHANGE", "#");

        //对每条消息设置过期时间
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2) //持久化消息
                .contentEncoding("UTF-8")
                .expiration("20000") //TTL
                .priority(5) //消息优先级，默认为：5。
                .build();


        //消息内容
        String msg = "hello world RabbitMQ! Msg DLX";

        channel.basicPublish("", "TEST_DLX_QUEUE", properties, msg.getBytes());


        channel.close();
        connection.close();
    }
}
