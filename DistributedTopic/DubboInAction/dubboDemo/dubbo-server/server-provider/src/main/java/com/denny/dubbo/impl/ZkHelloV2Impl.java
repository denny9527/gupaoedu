package com.denny.dubbo.impl;

import com.denny.dubbo.IZkHello;

public class ZkHelloV2Impl implements IZkHello {


    public String hello(String name) {
        return "Hello version 1.0.1 " + name ;
    }
}
