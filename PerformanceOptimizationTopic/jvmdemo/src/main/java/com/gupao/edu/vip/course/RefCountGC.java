package com.gupao.edu.vip.course;

public class RefCountGC {


   public Object instance = null;
   private byte[] bigSize = new byte[2* 1024 * 1024];

    public static void main(String[] args) {

        RefCountGC obj1 = new RefCountGC();
        RefCountGC obj2 = new RefCountGC();
        obj1.instance = obj2;
        obj2.instance = obj1;

        obj1 = null;
        obj2 = null;
        System.gc();

    }
}
