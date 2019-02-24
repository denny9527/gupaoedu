package com.denny.spring.design.pattern.singleton;

public class LazySingleton {

    private static LazySingleton INSTANCE;

    private LazySingleton(){

    }

    /**
     *  不管实例化与否都要进入同步块.
     * @return
     */
    public static synchronized LazySingleton getInstance(){
        if(INSTANCE == null)
            INSTANCE = new LazySingleton();
        return INSTANCE;
    }

}
