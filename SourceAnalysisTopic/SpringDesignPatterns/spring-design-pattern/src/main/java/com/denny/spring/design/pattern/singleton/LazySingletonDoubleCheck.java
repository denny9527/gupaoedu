package com.denny.spring.design.pattern.singleton;

public class LazySingletonDoubleCheck {

    private static volatile LazySingletonDoubleCheck INSTANCE;

    private LazySingletonDoubleCheck(){

    }

    /**
     *  不管实例化与否都要进入同步块.
     * @return
     */
    public static LazySingletonDoubleCheck getInstance() {
        if (INSTANCE == null){
            synchronized(LazySingletonDoubleCheck.class){
                if(INSTANCE == null)
                    INSTANCE = new LazySingletonDoubleCheck();
                return INSTANCE;
            }
        }
        return INSTANCE;
    }

}
