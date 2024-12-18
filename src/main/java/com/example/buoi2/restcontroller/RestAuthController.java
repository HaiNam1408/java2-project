package com.example.buoi2.restcontroller;

import com.example.buoi2.dto.AuthRequest;
import com.example.buoi2.dto.RegisterDto;
import com.example.buoi2.models.Role;
import com.example.buoi2.models.User;
import com.example.buoi2.repositories.RoleRepository;
import com.example.buoi2.repositories.UserRepository;
import com.example.buoi2.sercurity.JwtService;
import com.example.buoi2.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class RestAuthController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ApiResponse<String> registerUser(@RequestBody RegisterDto registerRequest) {
        Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());
        if (existingUser.isPresent()) {
            return new ApiResponse<>("Email is already in use!", 400, null);
        }

        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));

        User user = new User();
        user.setFirstname(registerRequest.getFirstname());
        user.setLastname(registerRequest.getLastname());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRoles(Set.of(defaultRole));

        // Lưu user vào cơ sở dữ liệu
        userRepository.save(user);

        return new ApiResponse<>("User registered successfully!", 201, null);
    }

    @PostMapping("/generateToken")
    @PreAuthorize("permitAll()") // Cho phép tất cả mọi người truy cập
    public ApiResponse<String> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        // Xác thực thông tin đăng nhập
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authRequest.getEmail(),
                                authRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            return new ApiResponse<>("Success", 200, token);
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

}
