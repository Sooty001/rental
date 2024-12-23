package com.example.rental.repositories;

import com.example.rental.models.User;

public interface UserRepository {
    User findUserByEmail(String email);
    String findByEmail(String email);
    User findById(Class<User> entityClass, int id);
    void create(User user);
}
