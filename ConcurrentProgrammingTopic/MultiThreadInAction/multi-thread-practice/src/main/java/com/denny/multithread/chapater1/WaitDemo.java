package com.denny.multithread.chapater1;

public class WaitDemo extends Thread {

    private Object object;

    public WaitDemo(Object object){
        this.object = object;
    }

    @Override
    public void run() {
        synchronized (object){
            System.out.println("开始执行线程！wait thread");
            try {
                object.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程执行结束！wait thread");
        }
    }
}
