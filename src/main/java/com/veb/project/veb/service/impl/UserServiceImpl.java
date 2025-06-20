package com.veb.project.veb.service.impl;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.model.User;
import com.veb.project.veb.repository.DocumentRepository;
import com.veb.project.veb.repository.UserRepository;
import com.veb.project.veb.repository.VersionHistoryRepository;
import com.veb.project.veb.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DocumentRepository documentRepository;
    private final VersionHistoryRepository versionHistoryRepository;
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, DocumentRepository documentRepository, VersionHistoryRepository versionHistoryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.documentRepository = documentRepository;
        this.versionHistoryRepository = versionHistoryRepository;
    }


    @Override
    public User registerUser(User user) {
        if (emailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }

        if (usernameExists(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists.");
        }

        if (user.getUsername().equalsIgnoreCase("admin")) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    //bidejki nemav dodadeno lista od dokumenti vo User i zatoa morav vaka racno
//    @Transactional
//    @Override
//    public void deleteById(Long id) {
//        Optional<User> optionalUser = userRepository.findById(id);
//        if (optionalUser.isPresent()) {
//            User user = optionalUser.get();
//
//            List<Document> userDocuments = documentRepository.findAllByUser(user);
//            for (Document doc : userDocuments) {
//                versionHistoryRepository.deleteAllByDocument(doc);
//                documentRepository.delete(doc);
//            }
//
//            userRepository.deleteById(id);
//        }
//    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void updateUser(Long id, User updatedUser) {
        User user=userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("User not found"));
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());

        userRepository.save(user);
    }
}
