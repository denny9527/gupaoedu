package com.denny.microservice.person.feign.client;

import com.denny.microservice.person.feign.client.ribbon.CustomConfig;
import com.denny.microservice.spring.cloud.feign.api.service.PersonService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.denny.microservice"})
@EnableFeignClients(clients = {PersonService.class})
@EnableEurekaClient //不启用Eureka注册，直接使用Feign 和 Ribbon进行客户端服务请求负载均衡
//说明CustomConfig配置的Ribbon相关配置将覆盖RibbonClientConfiguration中的配置。如：自定义负载均衡规则将覆盖默认的.
@RibbonClient(value = "person-service", configuration = CustomConfig.class)
@EnableHystrix
public class SpringCloudFeignClientApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringCloudFeignClientApplication.class)
                .run(args);
    }

}
