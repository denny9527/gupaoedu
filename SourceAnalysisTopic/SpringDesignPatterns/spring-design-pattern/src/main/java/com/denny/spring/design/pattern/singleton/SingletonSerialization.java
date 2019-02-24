package com.denny.spring.design.pattern.singleton;

import java.io.Serializable;

public class SingletonSerialization implements Serializable {

    private static final SingletonSerialization INSTANCE = new SingletonSerialization();

    private SingletonSerialization(){

    }

    public static SingletonSerialization getInstance(){

        return INSTANCE;

    }

    //防止反序列化单例失效
    private Object readResolve(){
        return INSTANCE;
    }
}
