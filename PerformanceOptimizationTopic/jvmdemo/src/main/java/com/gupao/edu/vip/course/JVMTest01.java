package com.gupao.edu.vip.course;

import java.util.ArrayList;
import java.util.List;

public class JVMTest01 {
    private static final String  a = new String("12");

    public static  JVMTest01 create(){
        return new JVMTest01();
    }

    byte[] byteArray = new byte[1 * 1024 * 1024];
    public static void main(String[] args) {
        JVMTest01 jvmTest01 = JVMTest01.create();
        List<JVMTest01> list = new ArrayList<>();
        int count = 0;
        try {
            while (true){
                list.add(new JVMTest01());
                count ++;
            }
        }catch (Throwable e){
            System.out.println("count:"+ count);
            e.printStackTrace();
        }

    }
}
