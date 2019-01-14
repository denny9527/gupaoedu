package com.denny.rabbitmq.returnlistener;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ReturnListenerProducer {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        //连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
//        //连接HOST
//        connectionFactory.setHost("127.0.0.1");
//        //连接端口号
//        connectionFactory.setPort(5672);
//        //虚拟机
//        connectionFactory.setVirtualHost("/");
//        //账号
//        connectionFactory.setUsername("guest");
//        //密码
//        connectionFactory.setPassword("guest");

        connectionFactory.setUri("amqp://guest:guest@127.0.0.1:5672");

        //连接
        Connection connection = connectionFactory.newConnection();

        //消息通道
        Channel channel = connection.createChannel();

        //声明交换机:交换机名称、交换机类型、是否持久(durable)、自动删除(autoDelete 即：交换机不再是使用时是否自动删除)、参数(arguments)
        channel.exchangeDeclare("TEST_DIRECT_EXCHANGE", "direct", true, false, null);

        //防止消息路由失败时丢失的方式：声明交换机时指定备份交换机
        //Map<String, Object> properies = new LinkedHashMap<String, Object>();
        //properies.put("alternate-exchange", "TEST_ALTERNATE_EXCHANGE");
        //channel.exchangeDeclare("TEST_DIRECT_EXCHANGE", "direct", true, false, properies);

        //声明队列:队列名称、是否持久(durable)、排他性(exclusive)、自动删除(autoDelete)、参数(arguments)
        channel.queueDeclare("TEST_QUQUE", true, false, false, null);

        channel.queueBind("TEST_QUQUE", "TEST_DIRECT_EXCHANGE", "denny.test");

        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
                System.out.println("=========监听器收到了无法路由，被返回的消息==========");
                System.out.println("replyText:"+replyText);
                System.out.println("exchange:"+exchange);
                System.out.println("routingKey:"+routingKey);
                System.out.println("message:"+new String(bytes));
            }
        });

        //消息内容
        String msg = "hello world RabbitMQ! 1000";

        channel.basicPublish("TEST_DIRECT_EXCHANGE", "hello", null, msg.getBytes());

        TimeUnit.SECONDS.sleep(10);

        channel.close();
        connection.close();
    }
}
