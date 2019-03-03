package com.gupao.edu.vip.course.chaper2;

public class SpaceGuarantee {

    public static void main(String[] args) {
        byte[] d1 = new byte[2 * 1024 * 1024];
        byte[] d2 = new byte[2 * 1024 * 1024];
        byte[] d3 = new byte[2 * 1024 * 1024];
        byte[] d4 = new byte[4 * 1024 * 1024];

        System.gc();

    }
}
