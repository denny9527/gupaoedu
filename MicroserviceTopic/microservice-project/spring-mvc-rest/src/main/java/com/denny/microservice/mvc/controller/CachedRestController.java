package com.denny.microservice.mvc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CachedRestController {

    @RequestMapping("/hello_world")
    @ResponseBody//没有缓存->304
    // 服务端和客户端没有形成默契（状态码）
    //HTTP协议，REST继承
    public String helloWorld(){//200、500、400
        return "Hello World!";//body="Hello World!"
    }


    @RequestMapping("/cache")
    public ResponseEntity<String> cache(){

        ResponseEntity<String> entity = new ResponseEntity<String>("hello world!", HttpStatus.NOT_MODIFIED);
        return entity;

    }

    @RequestMapping("/test_exception")
    public ResponseEntity<String> testException() throws Exception {

        throw new Exception("测试异常");

    }

    @RequestMapping("/testEtag")
    public ResponseEntity<String> testEtag(@RequestParam(value = "val", required = false) String val){

       if(null == val){
           val = "testEtag";
       }
       String etag =  DigestUtils.md5DigestAsHex(val.getBytes());

       ResponseEntity<String> entity = ResponseEntity.ok().eTag(etag).body("Hello World");

       return entity;
    }
}
