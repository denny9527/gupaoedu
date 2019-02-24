package com.denny.spring.design.pattern.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxy {

    private Subject subject = new RealSubject();

    public Subject getProxy(){
        Subject proxy = (Subject)Proxy.newProxyInstance(DynamicProxy.class.getClassLoader(), new Class[]{Subject.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("代理对象执行开始！");
                Object object = method.invoke(subject, args);
                System.out.println("代理对象执行结束！");
                return object;
            }
        });
        return proxy;
    }

}
