package com.denny.spring.design.pattern.singleton;

public class LazySingletonInner {

    public static LazySingletonInner getInstance(){
        return LazySingletonHolder.INSTANCE;
    }

    private LazySingletonInner(){}

    private static class LazySingletonHolder{
        private static final LazySingletonInner INSTANCE = new LazySingletonInner();
    }

}
