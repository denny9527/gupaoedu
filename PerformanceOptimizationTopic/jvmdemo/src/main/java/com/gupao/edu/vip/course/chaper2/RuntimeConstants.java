package com.gupao.edu.vip.course.chaper2;

public class RuntimeConstants {

   private int a = 1;

    public static void main(String[] args) {

      Class clazz =  RuntimeConstants.class;

        String s1 = "gupao";
        String s2 = "gupao";

        System.out.println(s1 == s2);
        String s3 = new String("gupao");
        System.out.println(s1 == s3);
        System.out.println(s1 == s3.intern());
    }
}
