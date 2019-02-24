package com.denny.spring.design.pattern.singleton;

import java.io.ObjectInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SinglegonRegister {

    private static Map<String, Object> SINGLETON_CACHE = new ConcurrentHashMap<String, Object>();

    static {
        SINGLETON_CACHE.put("com.denny.spring.design.pattern.singleton.RegSinglegon", new SinglegonRegister());
    }

    private SinglegonRegister(){}

    private static synchronized Object getInstance(String name){
        if(name == null){
            name = "com.denny.spring.design.pattern.singleton.RegSinglegon";
        }
        Object instance = SINGLETON_CACHE.get(name);
        if(instance == null){
            register(name);
            instance = SINGLETON_CACHE.get(name);
        }
        return instance;
    }

    private static void register(String className){
        Class instanceClass = null;
        try {
            instanceClass = Class.forName(className);
            SINGLETON_CACHE.putIfAbsent(className, instanceClass.newInstance());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }

    private static void unregister(String className){
        if(SINGLETON_CACHE.containsKey(className)){
            SINGLETON_CACHE.remove(className);
        }
    }

}
