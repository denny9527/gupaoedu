package com.denny.user.repository;

import com.denny.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class UserRepository {

    private static AtomicInteger idGenerator = new AtomicInteger();

    private static ConcurrentHashMap<String, User> userRepository = new ConcurrentHashMap<String, User>();

    public Collection<User> queryAllUsers() {
        return userRepository.values();
    }

    public boolean update(User user) {
        return true;
    }

    public User save(User user) {
        if(user != null){
            String userId = idGenerator.incrementAndGet() + "";
            user.setUserId(userId);
            userRepository.putIfAbsent(userId, user);
            return user;
        }
        return null;
    }
}
