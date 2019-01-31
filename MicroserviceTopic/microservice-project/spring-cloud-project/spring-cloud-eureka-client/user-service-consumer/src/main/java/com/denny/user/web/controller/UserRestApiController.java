package com.denny.user.web.controller;

import com.denny.user.domain.User;
import com.denny.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class UserRestApiController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/save")
    public User saveUser(@RequestParam String name){
        User user = new User();
        System.out.println("保存用户成功："+user.toString());
        return user;
    }

    @GetMapping("/user/list")
    public Collection<User> listUser(){
        return userService.queryAllUsers();
    }
}
