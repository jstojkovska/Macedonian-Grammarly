package com.veb.project.veb.service;

import com.veb.project.veb.model.User;

public interface UserService {
    User registerUser(User user);
    boolean usernameExists(String username);
    boolean emailExists(String email);
    User findByUsername(String username);
}
