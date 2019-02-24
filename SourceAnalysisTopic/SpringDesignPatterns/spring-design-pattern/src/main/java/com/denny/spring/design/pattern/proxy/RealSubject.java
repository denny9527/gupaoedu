package com.denny.spring.design.pattern.proxy;

public class RealSubject implements Subject {

    @Override
    public void operation() {
        System.out.println("目标对象执行！");
    }

}
