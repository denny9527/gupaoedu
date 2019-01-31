package com.denny.user.service;

import com.denny.user.domain.User;

import java.util.Collection;

public interface UserService {

    User save(User user);

    boolean update(User user);

    Collection<User> queryAllUsers();
}
