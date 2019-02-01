package com.denny.microservice.person.feign.client.controller;

import com.denny.microservice.spring.cloud.feign.api.domain.Person;
import com.denny.microservice.spring.cloud.feign.api.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class PersonClientController implements PersonService {

    @Autowired
    private PersonService personService;

    @Override
    public Person save(@RequestBody Person person){
        return personService.save(person);
    }

    @Override
    public Collection<Person> findAll(){
        return personService.findAll();
    }


}
