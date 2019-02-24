package com.denny.microservice.person.feign.service.provider.controller;

import com.denny.microservice.spring.cloud.feign.api.domain.Person;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class PersonServiceProviderController {

    private Map<Long, Person> persons = new ConcurrentHashMap<Long, Person>();

    @PostMapping("/person/save")
    @HystrixCommand(commandKey = "save(Person)", fallbackMethod = "fallbackSave", commandProperties={@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "6000")})
    public Person save(@RequestBody Person person){
        persons.put(person.getId(), person);
//        try {
//            Thread.sleep(15000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return person;
    }

    @GetMapping("/person/list")
    //defaultFallback:指定的回退方法不能带有参数
    @HystrixCommand(commandKey = "findAll()", defaultFallback = "fallbackFindAll", commandProperties={@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "6000")})
    public Collection<Person> findAll(){
//        try {
//            Thread.sleep(15000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        int randomMills = new Random().nextInt(1000);
//        System.out.println("findAll()方法耗时时间："+randomMills+"ms");
//        try {
//            Thread.sleep(randomMills);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return persons.values();
    }

    public Person fallbackSave(Person person){
        Person personTmp = new Person();
        personTmp.setId(0l);
        personTmp.setName("默认用户");
        return personTmp;
    }


    public Collection<Person> fallbackFindAll(){
        System.err.println("服务熔断fallbakcFindAll()方法被调用");
        return Collections.emptyList();
    }
}
