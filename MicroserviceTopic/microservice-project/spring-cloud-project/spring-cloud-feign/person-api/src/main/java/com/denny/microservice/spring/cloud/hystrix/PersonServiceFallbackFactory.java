package com.denny.microservice.spring.cloud.hystrix;

import com.denny.microservice.spring.cloud.feign.api.domain.Person;
import com.denny.microservice.spring.cloud.feign.api.service.PersonService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class PersonServiceFallbackFactory implements FallbackFactory<PersonService> {
    @Override
    public PersonService create(Throwable cause) {
        return new PersonService(){
            @Override
            public Person save(Person person) {
                Person personTmp = new Person();
                return personTmp;
            }

            @Override
            public Collection<Person> findAll() {
                return Collections.emptyList();
            }
        };
    }
}
