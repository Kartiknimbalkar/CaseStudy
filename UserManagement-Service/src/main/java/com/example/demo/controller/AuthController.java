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
import com.example.demo.entity.SignUpDto;
import com.example.demo.entity.User;
import com.example.demo.entity.UserDetails;
import com.example.demo.entity.UserUpdateDto;
import com.example.demo.exception.UsernameAlreadyTakenException;
import com.example.demo.repo.UserRepository;
//import com.example.demo.entity.AuthRequest;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;

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
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")					// accept username and password and provide a jwt token
    public ResponseEntity<String> login(@Valid @RequestBody AuthRequest request) {
        String token = authService.authenticate(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(token);
    }

    
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody SignUpDto request) {
        if (userRepository.existsById(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setContact(request.getContact());

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    
 // only authenticated users
    @GetMapping("/auth/me")
    public ResponseEntity<UserDetails> getCurrentUser(Authentication auth) {
        String username = auth.getName();
        UserDetails dto = userService.findByUsername(username);
        return ResponseEntity.ok(dto);
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/auth/users/{username}")
    public ResponseEntity<UserDetails> getUser(@PathVariable String username) {
    	UserDetails dto = userService.findByUsername(username);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("#username == authentication.name or hasAuthority('ADMIN')")
    @PutMapping("/auth/users/{username}")
    public ResponseEntity<User> updateUser(
        @PathVariable String username,
        @RequestBody UserUpdateDto incoming
    ) {
    	User updated = userService.updateProfile(username, incoming);
        return ResponseEntity.ok(updated);
    }

    
}
