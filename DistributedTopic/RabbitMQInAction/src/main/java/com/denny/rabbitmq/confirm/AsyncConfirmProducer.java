package com.denny.rabbitmq.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AsyncConfirmProducer {

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

        //声明交换机:交换机名称、交换机类型、是否持久(durable)、自动删除(autoDelete 即：交换机不再是使用时是否自动删除)、参数(arguments)
        channel.exchangeDeclare("TEST_DIRECT_EXCHANGE", "direct", true, false, null);

        //声明队列:队列名称、是否持久(durable)、排他性(exclusive)、自动删除(autoDelete)、参数(arguments)
        channel.queueDeclare("TEST_QUQUE", true, false, false, null);

        channel.queueBind("TEST_QUQUE", "TEST_DIRECT_EXCHANGE", "denny.test");

        //消息内容
        String msg = "hello world RabbitMQ! 1000";

        try{
            //开启Confirm模式
            channel.confirmSelect();

            channel.basicPublish("TEST_DIRECT_EXCHANGE", "denny.test", null, msg.getBytes());

            int i = 0;
            while( i < 50){
                channel.basicPublish("TEST_DIRECT_EXCHANGE", "denny.test", null, (msg + i).getBytes());
                i++;
            }

            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("Broker 确认消息，标识为："+deliveryTag+" multiple："+multiple);
                }

                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    //如果为：true表示批量执行了deliveryTag这个值以前的所有消息，如果为：false的话表示单条确认。
                    System.out.println("Broker 未确认消息，标识为："+deliveryTag+" multiple："+multiple);
                }
            });
            System.out.println("程序执行完成！");

        } catch (Exception e){
            System.out.println("消息发送失败！");
        }
        //channel.close();
        //connection.close();
    }
}
