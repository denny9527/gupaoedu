package com.denny.multithread.chapater1;

public class IncrSyncDemo {

    private static int count = 0;

    //private Object lock = new Object();//对象锁

    private static Object lock = new Object();//全局锁


    public static synchronized void incr(){//全局锁
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        count++;
        System.out.println("count="+count);
    }

    public void incr1(){
        //synchronized(ThreadDemo.class){ //全局锁。对类的所有对象实例和类本身。
        synchronized(this){ //对象锁。多个对象实例锁相互对立。
            count++;
            System.out.println("count="+count);
        }
    }

    public synchronized void incr2(){//对象锁。多个对象实例锁相互对立。
            count++;
            System.out.println("count="+count);
    }

    public  void incr3(){
        synchronized(lock){
            count++;
            System.out.println("count="+count);
        }
    }

    public static void main(String[] args) {
        for(int i = 0; i < 50; i++){
           Thread thread = new Thread(()->{
                incr();
            });
            thread.start();
//            try {
//                thread.join();//阻塞等待当前线程执行完
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
