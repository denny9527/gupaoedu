package com.denny.bio;

public class NIOTest {

    public static void main(String[] args) throws InterruptedException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Server.start();
            }
        }).start();

        Thread.sleep(3000);

        Client.send("hello world!");
    }
}
