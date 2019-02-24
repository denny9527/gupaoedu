package com.denny.multithread.chapater2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ConditionSignal extends Thread {

    private Lock lock;

    private Condition condition;

    public ConditionSignal(Lock lock, Condition condition) {
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void run() {
        lock.lock();
        System.out.println("ConditionSingal 执行启动！");
//        try {
//            Thread.sleep(20000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        condition.signal();
        System.out.println("ConditionSignal 执行结束！");
        lock.unlock();
    }

}
