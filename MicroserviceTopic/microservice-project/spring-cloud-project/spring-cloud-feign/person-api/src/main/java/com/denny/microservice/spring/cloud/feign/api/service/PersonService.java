package com.denny.microservice.spring.cloud.feign.api.service;

import com.denny.microservice.spring.cloud.feign.api.domain.Person;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

@FeignClient(value = "person-service") //服务提供方应用的名称
public interface PersonService {

    @PostMapping("/person/save")
    Person save(@RequestBody Person person);

    @GetMapping("/person/list")
    Collection<Person> findAll();

}
