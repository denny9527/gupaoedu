package com.denny.spring.design.pattern.prototype;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Prototype implements Cloneable, Serializable {

    private String name;

    private List<String> paramterList;

    public Prototype(String name, List<String> parameterList) {
        this.name = name;
        this.paramterList = parameterList;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        //return super.clone();//浅拷贝 paramterList 将引用原先的对象。可以手动深度拷贝。
        return deepClone();
    }

    /**
     * 采用反序列化 深度拷贝 Clone后Prototype.paramterList 将为新对象实例。
     * @return
     */
    private Object deepClone(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        Object obj = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(this);

            byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            obj = objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Prototype{");
        sb.append("name='").append(name).append('\'');
        sb.append(", paramterList=").append(paramterList);
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {

        List<String> paramterList = Arrays.asList("1", "2");
        Prototype prototype = new Prototype("denny", paramterList);

        try {
            Prototype clonePrototype = (Prototype)prototype.clone();
            System.out.println(clonePrototype.paramterList == prototype.paramterList);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
