package com.denny.spring.design.pattern.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CGLIBProxyDemo {

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(RealSubject.class);//设置父类
        enhancer.setCallback(new MethodInterceptor() {//设置方法拦截回调
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("代理对象执行开始！");
                Object obj = methodProxy.invokeSuper(o, args);
                System.out.println("代理对象执行结束！");
                return obj;
            }
        });
        RealSubject realSubject = (RealSubject)enhancer.create();//创建子类的实例对象
        realSubject.operation();
    }

}
