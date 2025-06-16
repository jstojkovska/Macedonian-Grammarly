package com.veb.project.veb.web;

import org.springframework.ui.Model;
import com.veb.project.veb.model.User;
import com.veb.project.veb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user, Model model){
        if(userService.emailExists(user.getEmail())){
            model.addAttribute("error","Email already exists.");
            return "register";
        }
        if (userService.usernameExists(user.getUsername())) {
            model.addAttribute("error", "Username already exists.");
            return "register";
        }
        if (user.getUsername().equalsIgnoreCase("admin")) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }
        userService.registerUser(user);
        return "redirect:/login";
    }
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/documents/home";
    }

}
