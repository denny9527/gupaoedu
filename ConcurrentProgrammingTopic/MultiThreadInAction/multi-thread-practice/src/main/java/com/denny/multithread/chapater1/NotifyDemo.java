package com.denny.multithread.chapater1;

public class NotifyDemo extends Thread {

    private Object object;

    public NotifyDemo(Object object){
        this.object = object;
    }

    @Override
    public void run() {
        synchronized (object){
            System.out.println("开始执行线程！notify thread");
            object.notify();
            System.out.println("线程执行结束！notify thread");
        }
    }
}
