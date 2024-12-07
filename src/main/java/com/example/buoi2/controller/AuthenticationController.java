package com.example.buoi2.controller;

import com.example.buoi2.dto.RegisterDto;
import com.example.buoi2.models.Role;
import com.example.buoi2.models.User;
import com.example.buoi2.repositories.RoleRepository;
import com.example.buoi2.repositories.UserRepository;
import com.example.buoi2.services.CompanyService;
import com.example.buoi2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class AuthenticationController {
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String showUserList(Model model) {
        List<User> users = userService.getAllUsers();

        model.addAttribute("users", users);
        return "userList";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new RegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterDto userRegister, Model model) {
        if (userRepository.existsByEmail(userRegister.getEmail())) {
            model.addAttribute("error", "Email already exists. Please use a different email.");
            return "register";
        }

        // Tạo user mới
        User user = new User();
        user.setFirstname(userRegister.getFirstname());
        user.setLastname(userRegister.getLastname());
        user.setEmail(userRegister.getEmail());
        user.setPassword(passwordEncoder.encode(userRegister.getPassword()));

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Collections.singleton(userRole));

        userRepository.save(user);
        return "redirect:/login";
    }

}
