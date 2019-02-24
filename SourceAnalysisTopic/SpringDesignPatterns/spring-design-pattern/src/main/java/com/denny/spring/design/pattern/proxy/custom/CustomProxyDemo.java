package com.denny.spring.design.pattern.proxy.custom;

import com.denny.spring.design.pattern.proxy.RealSubject;
import com.denny.spring.design.pattern.proxy.Subject;

import java.lang.reflect.Method;

public class CustomProxyDemo {

    public static void main(String[] args) {
        RealSubject realSubject = new RealSubject();

        Subject subject = (Subject) Proxy.newProxyInstance(CustomProxyDemo.class.getClassLoader(), new Class[]{Subject.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("代理对象执行开始！");
                Object object = method.invoke(realSubject, args);
                System.out.println("代理对象执行结束！");
                return object;
            }
        });
        subject.operation();

    }
}
