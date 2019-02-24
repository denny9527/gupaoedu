package com.denny.spring.design.pattern.singleton;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class SingletonTest {

    public static void main(String[] args) {
        //单例反射攻击
//        try {
//            Class singletonInnerClass = SingletonNoReflectAttack.class;
//            Constructor<LazySingletonInner> constructor = singletonInnerClass.getDeclaredConstructor(null);
//            constructor.setAccessible(true);
//            System.out.println(constructor.newInstance());
//            System.out.println(constructor.newInstance());
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }

        //序列化、反序列化
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream outputStream = null;

        FileInputStream fileInputStream = null;
        ObjectInputStream inputStream = null;
        try {

            SingletonSerialization instance = SingletonSerialization.getInstance();
            System.out.println("序列化前的实例："+instance);

            String userDir = (String)(System.getProperties().get("user.dir"));
            fileOutputStream = new FileOutputStream(userDir + "/singletonObject");
            outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(instance);

            fileInputStream = new FileInputStream(userDir + "/singletonObject");
            inputStream = new ObjectInputStream(fileInputStream);
            System.out.println("反序列化后的实例："+inputStream.readObject());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
