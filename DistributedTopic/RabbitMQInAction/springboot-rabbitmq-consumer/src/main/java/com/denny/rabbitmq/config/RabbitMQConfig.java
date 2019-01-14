package com.denny.rabbitmq.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class RabbitMQConfig {

    //定义ConnectionFactory
    @Bean
    public ConnectionFactory connectionFactory(){
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        return connectionFactory;

    }

    //设置缓存连接工厂
    @Bean
    public CachingConnectionFactory rabbitConnectionFactory(){
        CachingConnectionFactory rabbitConnectionFactory = new CachingConnectionFactory(connectionFactory());
        rabbitConnectionFactory.setPublisherConfirms(true);
        return rabbitConnectionFactory;
    }

    //设置RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory());
        return rabbitTemplate;
    }

    //设置消息监听容器
    @Bean
    public SimpleMessageListenerContainer listenerContainer(){
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(rabbitConnectionFactory());
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        simpleMessageListenerContainer.setPrefetchCount(5);
        ExecutorService executorService= Executors.newFixedThreadPool(300);
        simpleMessageListenerContainer.setTaskExecutor(executorService);
        simpleMessageListenerContainer.setConcurrentConsumers(200);

        return simpleMessageListenerContainer;
    }

    //定义交换机
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("TEST_DIRECT_EXCHANGE", true, false);
    }

    //定义队列
    @Bean
    public Queue queue(){
        return new Queue("TEST_QUEUE", true, false, false);
    }

    //定义绑定关系
    @Bean
    public Binding bind(@Qualifier("directExchange") DirectExchange exchange, @Qualifier("queue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with("denny.test");
    }

}
