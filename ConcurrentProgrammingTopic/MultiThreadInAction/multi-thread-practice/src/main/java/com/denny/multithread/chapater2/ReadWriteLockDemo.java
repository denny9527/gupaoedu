package com.denny.multithread.chapater2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockDemo {

    //共享锁：同时会有多个线程获取锁。

    private static Map<String, Object> cacheMap = new HashMap<String, Object>();

    private static ReentrantReadWriteLock wrLock = new ReentrantReadWriteLock();

    private static Lock readLock = wrLock.readLock();//读锁

    private static Lock writeLock = wrLock.writeLock();//写锁

    //缓存的更新和读取的时候
    public static Object get(String key){
        readLock.lock();//读锁
        try {
            return cacheMap.get(key);
        }finally {
            readLock.unlock();
        }
    }

    public static void put(String key, Object value){
        writeLock.lock();//写锁
        try {
            cacheMap.put(key, value);
        }finally {
            writeLock.unlock();
        }
    }

    public static void main(String[] args) {


    }
}
