package com.denny.microservice.person.feign.client;

import com.denny.microservice.person.feign.client.service.TestService;
import com.denny.microservice.spring.cloud.feign.api.domain.Person;
import org.junit.Test;
import org.junit.experimental.theories.internal.ParameterizedAssertionError;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringCloudFeignClientApplication.class})
public class SpingCloudFeignClientApplicationTests {

    @Autowired
    private TestService testService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testSavePerson(){
        Person person = new Person();
        person.setId(22222l);
        person.setName("张姓名");
        System.out.println(testService.save(person));
    }

    @Test
    public void testPersonList(){
        System.out.println(testService.findAll());
    }
}

