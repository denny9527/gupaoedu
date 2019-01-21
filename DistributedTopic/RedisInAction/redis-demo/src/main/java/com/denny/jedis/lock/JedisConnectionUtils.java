package com.denny.jedis.lock;

import jdk.internal.dynalink.beans.StaticClass;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisConnectionUtils {

    private final static JedisPool JEDIS_POOL;

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(100);
        JEDIS_POOL = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379);
    }

    public static Jedis getJedis(){
        return JEDIS_POOL.getResource();
    }

    public static void closeConnection(){
        JEDIS_POOL.close();
    }
}
