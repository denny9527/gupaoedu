package com.denny.user.service;

import com.denny.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@Service
public class UserProxy implements UserService {

    private static final String USER_SERVICE_PROVIDER_HOST = "http://user-service-provider";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public User save(User user) {
        user = restTemplate.postForObject(USER_SERVICE_PROVIDER_HOST + "/user/save", user, User.class);
        return user;
    }

    @Override
    public boolean update(User user) {
        return true;
    }

    @Override
    public Collection<User> queryAllUsers() {
        return restTemplate.getForObject(USER_SERVICE_PROVIDER_HOST + "/user/list", Collection.class);
    }
}
