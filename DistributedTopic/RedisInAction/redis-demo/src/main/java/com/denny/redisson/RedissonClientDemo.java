package com.denny.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

public class RedissonClientDemo {

    public static void main(String[] args) {
        //JedisPool 单机连接
        //哨兵机制
        //Config config = new Config();
        //config.useSentinelServers()
        //    .setMasterName("mymaster")
        //    .addSentinelAddress("redis://127.0.0.1:26389", "redis://127.0.0.1:26379")
        //    .addSentinelAddress("redis://127.0.0.1:26319");

        //redis cluster
        Config config = new Config();
        StringCodec stringCodec = new StringCodec();//使用String编码器。如使用默认的org.redisson.codec.JsonJacksonCodec 字符串将人为加入转义符，如："\"flag1\""
        config.setCodec(stringCodec);
        config.useClusterServers().addNodeAddress("redis://192.168.3.37:7000","redis://192.168.3.38:7003","redis://192.168.3.39:7006");

        RedissonClient redisClient = Redisson.create(config);
        //redisClient .getLock()//实现分布式锁
        redisClient.getBucket("test").set("ddddd");
        redisClient.getList("testList").add(1);
        redisClient.getList("testList").add(2);

        redisClient.getMap("testMap").put("flag1", 1);
        redisClient.getMap("testMap").put("flag2", 2);
        redisClient.getMap("testMap").put("flag3", 3);

        System.out.println(redisClient.getMap("testMap").get("flag3"));
        System.out.println(redisClient.getMap("testMap").keySet());
    }
}
