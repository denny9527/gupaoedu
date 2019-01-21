package com.denny.redisson.lock;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

public class RedissonLockDemo implements Runnable {

    private RLock lock;

    public RedissonLockDemo() {
        Config config = new Config();
        StringCodec stringCodec = new StringCodec();//使用String编码器。如使用默认的org.redisson.codec.JsonJacksonCodec 字符串将人为加入转义符，如："\"flag1\""
        config.setCodec(stringCodec);
        //config.useClusterServers().addNodeAddress("redis://192.168.3.37:7000","redis://192.168.3.38:7003","redis://192.168.3.39:7006");
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redissonClient = Redisson.create(config);
        this.lock = redissonClient.getLock("testLock");
    }

    public static void main(String[] args) {
        RedissonLockDemo lockTest = new RedissonLockDemo();
        int i = 0;
        while (i < 10){
            new Thread(lockTest, "ThreadName"+i).start();
            i++;
        }

    }

    @Override
    public void run() {
        while (true){
            try {
                boolean isAcquire = this.lock.tryLock(1000, 2000, TimeUnit.SECONDS);
                if (isAcquire) {
                    System.out.println(Thread.currentThread().getName() + "->" + "成功获取锁!");
                    try {
                        Thread.sleep(1000);
                        this.lock.unlock();
                        System.out.println(Thread.currentThread().getName() + "->" + "释放锁!");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                if(this.lock.isLocked()) this.lock.unlock();
            }
        }
    }
}
