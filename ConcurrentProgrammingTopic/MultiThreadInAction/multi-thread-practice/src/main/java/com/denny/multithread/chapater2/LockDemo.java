package com.denny.multithread.chapater2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockDemo {

    private static Lock lock = new ReentrantLock();//公平锁和非公平锁

    private static int count = 0;

    public static synchronized void incr(){//全局锁
        lock.lock();//获取锁（CXQ和EntryList）
        count++;
        System.out.println("count="+count);
        lock.unlock();//释放锁
    }

    public static void main(String[] args) {
        for(int i = 0; i < 2; i++){
            Thread thread = new Thread(()->{
                incr();
            });
            thread.start();
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
