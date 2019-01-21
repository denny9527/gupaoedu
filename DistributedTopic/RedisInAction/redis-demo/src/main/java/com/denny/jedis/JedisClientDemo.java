package com.denny.jedis;

import com.denny.jedis.lock.JedisConnectionUtils;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;

public class JedisClientDemo {

    public static void main(String[] args) {
        //sentinel 哨兵模式下如何连接redis server
        //HostAndPort hostAndPort = new HostAndPort("mymaster", );
        //哨兵集群
        //JedisSentinelPool sentinelPool = new JedisSentinelPool();

        //redis cluster连接
        Set<HostAndPort> hostAndPortSet = new HashSet<HostAndPort>();//master节点配置
        hostAndPortSet.add(new HostAndPort("192.168.3.37", 7000));
        hostAndPortSet.add(new HostAndPort("192.168.3.38", 7003));
        hostAndPortSet.add(new HostAndPort("192.168.3.39", 7006));
        JedisCluster jedisCluster = new JedisCluster(hostAndPortSet);//建立路由规则

        jedisCluster.set("test", "helloworld OK");
        //System.out.println(jedisCluster.hget("testMap", "\"flag3\""));

        Jedis jedis = JedisConnectionUtils.getJedis();
        ((Jedis) jedis).close();
        JedisConnectionUtils.closeConnection();
    }
}
