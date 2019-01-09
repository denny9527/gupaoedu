package com.denny.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;


import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerDemo extends Thread {

    private KafkaConsumer<Integer, String> kafkaConsumer;

    private String topic;

    public KafkaConsumerDemo(String topic) {

        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.3.14:9092,192.168.3.36:9092,192.168.3.37:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaConsumerDemo");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earlist");
        this.kafkaConsumer = new KafkaConsumer<Integer, String>(properties);
        kafkaConsumer.subscribe(Collections.singletonList("test"));
        this.topic = topic;
    }

    @Override
    public void run() {
        while(true){
            ConsumerRecords<Integer, String> consumerRecords = kafkaConsumer.poll(1000);
            for(ConsumerRecord record : consumerRecords){
                System.out.println("接收到的消息："+record.value());
            }
        }
    }

    public static void main(String[] args){
        new KafkaConsumerDemo("test").start();
    }
}
