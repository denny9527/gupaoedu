
package com.gupao.edu.vip.lion.cache.redis.manager;

import com.gupao.edu.vip.lion.cache.redis.RedisServer;

import java.util.List;

public interface RedisClusterManager {

    void init();

    List<RedisServer> getServers();
}
