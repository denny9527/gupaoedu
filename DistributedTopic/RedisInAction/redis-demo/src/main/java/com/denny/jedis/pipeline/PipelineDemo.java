package com.denny.jedis.pipeline;

import com.denny.jedis.lock.JedisConnectionUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Pipeline;

import java.util.HashSet;
import java.util.Set;

public class PipelineDemo {

    public static void main(String[] args) {
        Jedis jedis = JedisConnectionUtils.getJedis();
        Pipeline pipeline = jedis.pipelined();
        pipeline.set("test1", "hello1");
        pipeline.set("test2", "hello2");
        pipeline.set("test3", "hello3");
        pipeline.set("test4", "hello4");
        pipeline.sync();//一次性提交
    }
}

