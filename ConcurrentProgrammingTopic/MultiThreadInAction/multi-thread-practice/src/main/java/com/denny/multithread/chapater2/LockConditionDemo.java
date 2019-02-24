package com.denny.multithread.chapater2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockConditionDemo {

    public static void main(String[] args) {

        Lock lock = new ReentrantLock(true);
        Condition condition = lock.newCondition();

        ConditionWait conditionWait = new ConditionWait(lock, condition);

        ConditionSignal conditionSignal = new ConditionSignal(lock, condition);

        conditionWait.start();

        conditionSignal.start();

    }
}
