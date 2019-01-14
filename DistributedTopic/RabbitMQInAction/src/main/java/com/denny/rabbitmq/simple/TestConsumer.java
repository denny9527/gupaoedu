package com.denny.rabbitmq.simple;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 在消费者端声明交换机和绑定的队列，RabbitMQ将创建指定的交换机、队列和绑定关系。无需在管理UI上创建
 */
public class TestConsumer {

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
        final Channel channel = connection.createChannel();

        //声明交换机:交换机名称、交换机类型、是否持久(durable)、自动删除(autoDelete 即：交换机不再是使用时是否自动删除)、参数(arguments)
        channel.exchangeDeclare("TEST_DIRECT_EXCHANGE", "direct", true, false, null);

        //声明队列:队列名称、是否持久(durable)、排他性(exclusive)、自动删除(autoDelete)、参数(arguments)
        channel.queueDeclare("TEST_QUQUE", true, false, false, null);

        channel.queueBind("TEST_QUQUE", "TEST_DIRECT_EXCHANGE", "denny.test");

        //创建消费者
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println("接受到消息内容："+msg);
                System.out.println("消费者标签："+consumerTag);
                System.out.println("投递标签："+envelope.getDeliveryTag());
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        //获取消息：队列名称、是否自动确认(autoAck)、消费者
        //自动确认到消息从对列中推送到消费者时即从队列中删除该条消息
        //手动确认到队列推送消息进入handleDelivery方法中并调用了basicAck才完成消息确认，此时队列中才会删除该消息。
        channel.basicConsume("TEST_QUQUE", true, consumer);
    }

}
