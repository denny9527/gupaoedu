package com.denny.rabbitmq.dlx;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 演示当队列中的堆积消息超过指定的队列最大长度，先入队的消息将删除进入死信队列。
 * 演示拒绝或不应答，并且拒绝重新入队。消息将进入死信队列。
 *
 */
public class TestDLXConsumer {
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


        Map<String, Object> basicProperties = new LinkedHashMap<String, Object>();
        basicProperties.put("x-dead-letter-exchange", "DLX_EXCHANGE");//指定死信交换机
        basicProperties.put("x-max-length", 4);//指定队列大小

        //使用RabbitMQ默认交换机
        channel.queueDeclare("TEST_DLX_QUEUE", false, false, false, basicProperties);

        //声明死信交换机
        channel.exchangeDeclare("DLX_EXCHANGE", "topic", false, false, false, null);

        //声明死信队列
        channel.queueDeclare("DLX_QUEUE", false, false, false, null);

        //绑定死信交换机和死信队列
        channel.queueBind("DLX_QUEUE", "DLX_EXCHANGE", "#");


        //消息内容
        String msg = "hello world RabbitMQ! Msg TTL";


        //创建消费者
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                String num = msg.substring(msg.length() - 1);
                if(Integer.valueOf(num) > 3){
                    channel.basicReject(envelope.getDeliveryTag(), false);//拒绝或不应答，并且拒绝重新入队
                }
                System.out.println("接受到消息内容："+msg);
                System.out.println("消费者标签："+consumerTag);
                System.out.println("投递标签："+envelope.getDeliveryTag());
            }
        };
        //获取消息：队列名称、是否自动确认(autoAck)、消费者
        channel.basicConsume("TEST_DLX_QUEUE", false, consumer);

        //发送消息
        Thread  thread = new Thread(){

            @Override
            public void run() {
                int i = 0;
                while( i < 10){
                    //消息内容
                    String msg = "hello world RabbitMQ! Msg DLX "+i;

                    try {
                        System.out.println("发送消息："+msg);
                        channel.basicPublish("", "TEST_DLX_QUEUE", null, msg.getBytes());
                        Thread.sleep(1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;

                }
            }
        };
        thread.start();
        //channel.close();
        //connection.close();
    }
}
