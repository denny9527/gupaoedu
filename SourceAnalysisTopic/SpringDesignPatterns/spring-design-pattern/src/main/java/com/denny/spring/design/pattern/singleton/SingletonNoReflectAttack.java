package com.denny.spring.design.pattern.singleton;

public class SingletonNoReflectAttack {

    private static boolean IS_INSTANCED = false;

    private static final SingletonNoReflectAttack INSTANCE = new SingletonNoReflectAttack();

    private SingletonNoReflectAttack(){

        synchronized(SingletonNoReflectAttack.class){
            if(!IS_INSTANCED){
                IS_INSTANCED = !IS_INSTANCED;
            }else{
                throw new RuntimeException("单例已产生！");
            }

        }

    }

    public static SingletonNoReflectAttack getInstance(){
        return INSTANCE;

    }
}
