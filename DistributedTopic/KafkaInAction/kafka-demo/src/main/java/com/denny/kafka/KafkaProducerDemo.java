package com.denny.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaProducerDemo extends Thread {


    private KafkaProducer<Integer, String> producer;

    private String topic;

    KafkaProducerDemo(String topic){
        this.topic = topic;
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.3.14:9092,192.168.3.36:9092,192.168.3.37:9092");
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaProducerDemo");
        properties.put(ProducerConfig.ACKS_CONFIG, "-1");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<Integer, String>(properties);
        this.topic = topic;
    }

    public static void main(String[] args){
        KafkaProducerDemo kafkaProducerDemo = new KafkaProducerDemo("test");
        kafkaProducerDemo.start();
    }

    @Override
    public void run() {
        int loop = 0;

        while(loop < 50){
            String message = "message_"+loop;
            System.out.println("发送的消息为："+message);
            this.producer.send(new ProducerRecord<Integer, String>(topic, message));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loop++;
        }

        super.run();
    }
}
