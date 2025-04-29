package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.example.demo.util.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    public String authenticate(String username, String password) {
        // authenticate the user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        // load user details after successful authentication
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        return jwtUtil.generateToken(userDetails);
    }
    
    public boolean validateToken(String token) {		// validate the token
    	String username = jwtUtil.extractUsername(token);
    	UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    	return jwtUtil.validateToken(token, userDetails);
    }
    
}
