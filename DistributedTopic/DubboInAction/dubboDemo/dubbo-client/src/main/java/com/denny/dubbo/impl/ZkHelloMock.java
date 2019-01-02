package com.denny.dubbo.impl;

import com.denny.dubbo.IZkHello;

public class ZkHelloMock implements IZkHello {

    public String hello(String s) {
        return "系统繁忙："+s;
    }
}
