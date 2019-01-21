package com.denny.jedis.lock;

import java.awt.*;

public class LockTest implements Runnable {

    @Override
    public void run() {
        while (true){
            DistributedLock distributedLock = new DistributedLock();
            String rs = distributedLock.acquireLock("updateOrder", 2000, 5000);
            if(null != rs){
                System.out.println(Thread.currentThread().getName()+"->"+"成功获取锁："+rs);
                try {
                    Thread.sleep(1000);
                    //distributedLock.releaseLock("updateOrder", rs);
                    distributedLock.releaseLockWithLua("updateOrder", rs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        System.out.println(Thread.currentThread().getName()+"线程执行成功");
    }

    public static void main(String[] args) {
        LockTest lockTest = new LockTest();
        int i = 0;
        while (i < 10){
            new Thread(lockTest, "ThreadName"+i).start();
            i++;
        }
    }
}
