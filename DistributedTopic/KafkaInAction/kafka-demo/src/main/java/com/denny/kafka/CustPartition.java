package com.denny.kafka;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 指定消息分区策略
 */
public class CustPartition implements Partitioner {

    private Random random = new Random();

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        //获取当前有效分区列表
        List<PartitionInfo> partitions = cluster.availablePartitionsForTopic(topic);
        int partitionNum = 0;
        if(null == key){
            partitionNum = random.nextInt(partitions.size());
        }else {
            partitionNum = Math.abs(key.hashCode()) % partitions.size();
        }
        System.out.println("key:"+(key != null? key.toString() : "")+" value:"+value.toString()+"在分区:"+partitionNum+"中");
        return partitionNum;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
