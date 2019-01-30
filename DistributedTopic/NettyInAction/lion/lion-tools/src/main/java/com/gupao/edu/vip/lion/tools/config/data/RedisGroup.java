
package com.gupao.edu.vip.lion.tools.config.data;

import java.util.Collections;
import java.util.List;


/**
 * redis 组
 */
public class RedisGroup {
    public List<RedisNode> redisNodeList = Collections.emptyList();

    public RedisGroup() {
    }

    public RedisGroup(List<RedisNode> redisNodeList) {
        this.redisNodeList = redisNodeList;
    }

    @Override
    public String toString() {
        return "RedisGroup{" +
                "redisNodeList=" + redisNodeList +
                '}';
    }
}
