package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.AuthRequest;
import com.example.demo.entity.User;
import com.example.demo.exception.UsernameAlreadyTakenException;
import com.example.demo.repo.UserRepository;
//import com.example.demo.entity.AuthRequest;
import com.example.demo.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")					// accept username and password and provide a jwt token
    public ResponseEntity<String> login(@Valid @RequestBody AuthRequest request) {
        String token = authService.authenticate(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(token);
    }

    
    @PostMapping("/register")  // sign up (register a new user)
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        // Check if a user with the same username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyTakenException("Username is already taken. Please choose a different username.");
        }
        
        // Encode the password before saving the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Save the new user
        User savedUser = userRepository.save(user);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    
}
