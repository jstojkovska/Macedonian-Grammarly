package com.veb.project.veb.service;

import com.veb.project.veb.model.User;
import org.springframework.expression.spel.ast.OpAnd;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    boolean usernameExists(String username);
    boolean emailExists(String email);
    User findByUsername(String username);
    List<User> findAll();
    Optional<User> findById(Long id);
    void deleteById(Long id);
    User save(User user);
    void updateUser(Long id,User updatedUser);

}
