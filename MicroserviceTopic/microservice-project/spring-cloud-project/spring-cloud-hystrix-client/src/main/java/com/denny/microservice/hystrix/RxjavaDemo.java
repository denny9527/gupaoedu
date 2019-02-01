package com.denny.microservice.hystrix;

import rx.Single;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.util.Random;

public class RxjavaDemo {

    public static void main(String[] args) {

        Single.just("hello world") // just 发布数据
                .subscribeOn(Schedulers.immediate())//订阅的线程池，immediate = Thread.crruentThread
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() { //正常流程
                        System.out.println("执行结束！");
                    }

                    @Override
                    public void onError(Throwable e) { //异常流程
                        System.out.println("熔断保护");
                    }

                    @Override
                    public void onNext(String s) { //数据消费
                        int value = new Random().nextInt(200);
                        System.out.println("helloWorld() cost:"+value+"ms");
                        if(value > 100){
                            throw  new RuntimeException("Timeout!");
                        }
                    }
                });
    }
}
