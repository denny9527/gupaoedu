package com.denny.multithread.chapater2;

import java.util.concurrent.locks.LockSupport;

public class ParkAndUnparkDemo extends Thread {

    @Override
    public void run() {
        LockSupport.park();
    }

    public static void main(String[] args) {

        System.out.println(Thread.interrupted());
    }
}
