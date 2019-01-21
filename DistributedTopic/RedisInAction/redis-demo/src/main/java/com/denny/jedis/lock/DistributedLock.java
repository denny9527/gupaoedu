package com.denny.jedis.lock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.UUID;
import java.util.concurrent.locks.Lock;

public class DistributedLock {

    /**
     *
     * @param lockName 锁的名称
     * @param acquireTimeout 获取锁的超时时间
     * @param lockTimeout 锁本身的超时时间
     * @return
     */
    public String acquireLock(String lockName, long acquireTimeout, long lockTimeout){
        String identifier = UUID.randomUUID().toString();//保证释放锁时是同一个持有者
        String lockKey = "lock:"+lockName;
        int lockExpire = (int)(lockTimeout/1000);
        Jedis jedis = null;

        jedis = JedisConnectionUtils.getJedis();

        long end = System.currentTimeMillis() + acquireTimeout;
        try {
            //获取锁的限定时间
            while (System.currentTimeMillis() <= end) {
                try {
                    Long result = jedis.setnx(lockKey, identifier);
                    if (result == 1) { //设置值成功
                        jedis.expire(lockKey, lockExpire); //设置锁的超时时间
                        return identifier; //成功获取锁
                    }

                    if (jedis.ttl(lockKey) == -1) { //未设置超时时间
                        jedis.expire(lockKey, lockExpire); //设置锁的超时时间
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }finally {
            jedis.close();
        }
        return null;
    }

    public boolean releaseLockWithLua(String lockName, String identifier){
        System.out.println(lockName+"开始释放锁："+identifier);
        Jedis jedis = JedisConnectionUtils.getJedis();
        String lockKey = "lock:"+lockName;
        String lua = "if (redis.call('get', KEYS[1]) == ARGV[1])"+
                " then return redis.call('del', KEYS[1])"+
                " else return 0 end";
        Long rs = (Long)jedis.eval(lua, 1, new String[]{lockKey, identifier});
        if(rs.intValue() > 0){
            return true;
        }
        jedis.close();
        return false;

    }

    public boolean releaseLock(String lockName, String identifier){
        System.out.println(lockName+"开始释放锁："+identifier);
        String lockKey = "lock:"+lockName;
        boolean isRelease = false;
        Jedis jedis = null;
        jedis = JedisConnectionUtils.getJedis();
        try {
            while (true) {
                jedis.watch(lockKey);//开启事务，保证受监控的键未被修改
                if (identifier.equals(jedis.get(lockKey))) {
                    Transaction transaction = jedis.multi();
                    transaction.del(lockKey);//发起命令入队
                    if (transaction.exec().isEmpty()) {//exec执行入队命令
                        continue;
                    }
                    isRelease = true;
                }
                jedis.unwatch();
                break;
            }
        }finally {
            jedis.close();
            System.out.println("jedis连接已关闭");
        }

        return isRelease;
    }
}
