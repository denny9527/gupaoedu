package com.denny.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {UserConsumerBootstrap.class})
@SpringBootTest
public class RestTemplateTest {

    private RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    @Test
    public void contextLoads() {

    }

    @Test
    public void testRest(){
        System.out.println(restTemplate.getForObject("http://127.0.0.1:8080/actuator/", String.class));
    }
}

