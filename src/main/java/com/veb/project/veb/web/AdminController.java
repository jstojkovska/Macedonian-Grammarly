package com.veb.project.veb.web;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.model.User;
import com.veb.project.veb.model.VersionHistory;
import com.veb.project.veb.repository.UserRepository;
import com.veb.project.veb.service.DocumentService;
import com.veb.project.veb.service.UserService;
import com.veb.project.veb.service.VersionHistoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final DocumentService documentService;
    private final VersionHistoryService versionHistoryService;

    public AdminController(UserService userService, DocumentService documentService, VersionHistoryService versionHistoryService) {
        this.userService = userService;
        this.documentService = documentService;
        this.versionHistoryService = versionHistoryService;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin-users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow();
        model.addAttribute("user", user);
        return "admin-edit-user";
    }

    @PostMapping("/users/edit/{id}")
    public String editUserSubmit(@PathVariable Long id, @ModelAttribute User updatedUser) {
        userService.updateUser(id, updatedUser);
        return "redirect:/admin/users";
    }


    @GetMapping("/users/{id}/documents")
    public String viewUserDocuments(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        List<Document> documents = documentService.getAllForUser(user.getUsername());

        Map<Long, List<VersionHistory>> versionsMap = versionHistoryService.getVersionsForDocuments(documents);

        model.addAttribute("documents", documents);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("versionsMap", versionsMap);
        model.addAttribute("userId", user.getId());
        return "user-documents";
    }

}
