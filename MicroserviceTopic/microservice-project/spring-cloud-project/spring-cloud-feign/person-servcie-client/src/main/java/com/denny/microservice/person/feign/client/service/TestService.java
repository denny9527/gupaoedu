package com.denny.microservice.person.feign.client.service;

import com.denny.microservice.spring.cloud.feign.api.domain.Person;
import com.denny.microservice.spring.cloud.feign.api.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

@Service
public class TestService {

    @Autowired
    private PersonService personService;

    public Person save(Person person){
        return personService.save(person);
    }

    public Collection<Person> findAll(){
        return personService.findAll();
    }

}
