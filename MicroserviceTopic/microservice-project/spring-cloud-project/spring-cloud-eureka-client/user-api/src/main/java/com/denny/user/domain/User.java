package com.denny.user.domain;

import lombok.Data;

@Data
public class User {

    private String name;

    private String userId;

    private String male;

    private Integer age;

    public User() {
    }

    public User(String name, String userId, String male, Integer age) {
        this.name = name;
        this.userId = userId;
        this.male = male;
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", male='" + male + '\'' +
                ", age=" + age +
                '}';
    }
}
