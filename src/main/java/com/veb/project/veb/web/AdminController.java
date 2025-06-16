package com.veb.project.veb.web;
import com.veb.project.veb.model.Document;
import com.veb.project.veb.model.User;
import com.veb.project.veb.repository.UserRepository;
import com.veb.project.veb.service.DocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final DocumentService documentService;

    public AdminController(UserRepository userRepository, DocumentService documentService) {
        this.userRepository = userRepository;
        this.documentService = documentService;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin-users";
    }
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id){
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);
        return "admin-edit-user";
    }

    @PostMapping("/users/edit/{id}")
    public String editUserSubmit(@PathVariable Long id, @ModelAttribute User updatedUser) {
        User user = userRepository.findById(id).orElseThrow();
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        userRepository.save(user);
        return "redirect:/admin/users";
    }
    @GetMapping("/users/{id}/documents")
    public String viewUserDocuments(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        List<Document> documents = documentService.getAllForUser(user.getUsername());
        model.addAttribute("documents", documents);
        model.addAttribute("username", user.getUsername());
        return "user-documents";
    }



}
