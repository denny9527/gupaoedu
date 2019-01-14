package com.denny.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerDemo1 extends Thread {

    private KafkaConsumer<Integer, String> kafkaConsumer;

    private String topic;

    public KafkaConsumerDemo1(String topic) {

        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.3.14:9092,192.168.3.36:9092,192.168.3.37:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaConsumerDemo1");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); //true：自动提交；false：手动提交
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        //properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "none");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
        this.kafkaConsumer = new KafkaConsumer<Integer, String>(properties);
        //TopicPartition assignPartition = new TopicPartition("test", 2);
        //kafkaConsumer.assign(Arrays.asList(assignPartition));//指定消费的分区
        kafkaConsumer.subscribe(Collections.singletonList("test"));
        this.topic = topic;
    }

    @Override
    public void run() {
        while(true){
            ConsumerRecords<Integer, String> consumerRecords = kafkaConsumer.poll(1000);
            for(ConsumerRecord record : consumerRecords){
                System.out.println("接收到的消息："+record.value()+" 所在分区为："+record.partition());
                kafkaConsumer.commitAsync(); //异步手动提交确认
            }
        }
    }

    public static void main(String[] args){
        new KafkaConsumerDemo1("test").start();
    }
}
