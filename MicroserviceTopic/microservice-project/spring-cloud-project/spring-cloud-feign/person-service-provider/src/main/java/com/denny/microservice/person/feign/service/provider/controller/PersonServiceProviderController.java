package com.denny.microservice.person.feign.service.provider.controller;

import com.denny.microservice.spring.cloud.feign.api.domain.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class PersonServiceProviderController {

    private Map<Long, Person> persons = new ConcurrentHashMap<Long, Person>();

    @PostMapping("/person/save")
    public Person save(@RequestBody Person person){
        persons.put(person.getId(), person);
        return person;
    }

    @GetMapping("/person/list")
    public Collection<Person> findAll(){
        return persons.values();
    }

}
