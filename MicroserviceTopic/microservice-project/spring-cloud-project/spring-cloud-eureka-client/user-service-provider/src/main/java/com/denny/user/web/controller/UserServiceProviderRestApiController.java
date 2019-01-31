package com.denny.user.web.controller;

import com.denny.user.domain.User;
import com.denny.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class UserServiceProviderRestApiController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/save")
    public User saveUser(@RequestBody User user){
        return userService.save(user);
    }

    @GetMapping("/user/list")
    public Collection<User> listUser(){
        return userService.queryAllUsers();
    }

}
