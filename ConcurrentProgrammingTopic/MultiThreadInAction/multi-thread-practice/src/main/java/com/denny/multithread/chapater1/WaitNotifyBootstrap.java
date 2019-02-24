package com.denny.multithread.chapater1;

public class WaitNotifyBootstrap {

    public static void main(String[] args) {
        Object object = new Object();
        WaitDemo waitDemo = new WaitDemo(object);
        NotifyDemo notifyDemo = new NotifyDemo(object);
        waitDemo.start();
        notifyDemo.start();
    }
}
