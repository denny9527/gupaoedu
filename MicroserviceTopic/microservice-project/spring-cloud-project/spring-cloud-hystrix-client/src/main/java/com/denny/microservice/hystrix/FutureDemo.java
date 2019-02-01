package com.denny.microservice.hystrix;

import java.util.Random;
import java.util.concurrent.*;

public class FutureDemo {

    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(20);

        Future<String> future = service.submit(()->{
            int value = new Random().nextInt(2400);
            Thread.sleep(value);
            return "Hello world!";
        });

        try {
            System.out.println(future.get(2000, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            System.out.println("超时保护！");
        }
        service.shutdown();//等待已提交任务都完成后才会shutdown线程池。

    }
}
