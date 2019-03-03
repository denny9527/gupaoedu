package com.gupao.edu.vip.course;

import java.util.Random;

public class OOMTest {


    
    public static void main(String[] args) {
        String str = "www.google.com" ;
        while(true) {
            str += str + new Random().nextInt(88888888) + new Random().nextInt(999999999);

        }
    }
}
