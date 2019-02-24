package com.denny.microservice.spring.cloud.hystrix;

import com.denny.microservice.spring.cloud.feign.api.domain.Person;
import com.denny.microservice.spring.cloud.feign.api.service.PersonService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class PersonServiceFallback implements PersonService {

    @Override
    public Person save(Person person) {
        Person personTmp = new Person();
        return personTmp;
    }

    @Override
    public Collection<Person> findAll() {
        return Collections.emptyList();
    }
}
