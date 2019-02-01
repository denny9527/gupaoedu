package com.denny.microservice.hystrix.client.controller;



import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.Future;

@RestController
public class HystrixDemoController {

    private Random random = new Random();

    /**
     * 当方法调用失败时，
     * fallback 方法{@link #errorContent()}作为替代返回
     *
     * @return
     */
    @GetMapping("/hello_world")
    //设置超时时间
    @HystrixCommand(fallbackMethod = "errorContent",
            commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")}
    )
    public String helloWorld() throws InterruptedException {

        int value = random.nextInt(2000);

        System.out.println("等待时间：" + value + "ms");

        Thread.sleep(Long.valueOf(value));

        return "Hello World!";
    }

    public String errorContent() {
        return "error";
    }

    @GetMapping("/hello_world_1")
    public String helloWorld1(){
        return new HelloWorldCommand().execute();
    }

    private static class HelloWorldCommand extends com.netflix.hystrix.HystrixCommand<String> {

        public HelloWorldCommand(){
            super(HystrixCommandGroupKey.Factory.asKey("Hello World"), 1500);
        }

        @Override
        protected String getFallback() {
            return "time out!";
            //return super.getFallback();
        }

        @Override
        public String execute() {
            return super.execute();
        }

        @Override
        public Future queue() {
            return super.queue();
        }

        @Override
        protected String getFallbackMethodName() {
            return super.getFallbackMethodName();
        }

        @Override
        protected boolean isFallbackUserDefined() {
            return super.isFallbackUserDefined();
        }

        @Override
        protected boolean commandIsScalar() {
            return super.commandIsScalar();
        }

        @Override
        protected String run() throws Exception {
            Random random = new Random();
            int value = random.nextInt(2000);

            System.out.println("等待时间：" + value + "ms");

            Thread.sleep(Long.valueOf(value));
            return "Hello world!";
        }
    }
}
