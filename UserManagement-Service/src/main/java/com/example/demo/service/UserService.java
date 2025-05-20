package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.AuthRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.UserDetails;
import com.example.demo.entity.UserUpdateDto;
import com.example.demo.repo.UserRepository;

@Service
public class UserService {
  @Autowired private UserRepository repo;
  @Autowired private PasswordEncoder encoder;

  public UserDetails findByUsername(String username) {
	    User u = repo.findById(username)
	                 .orElseThrow(() -> new UsernameNotFoundException(username));
	    return new UserDetails(u);  // returns with additional fields
	}


  @Transactional
  public User updateProfile(String username, UserUpdateDto dto) {
    User u = repo.findById(username)
                 .orElseThrow(() -> new UsernameNotFoundException(username));
    // apply updates
//    u.setPassword(encoder.encode(dto.getPassword()));
//    u.setRole(dto.getRole());
    u.setPassword(encoder.encode(dto.getPassword()));
    u.setRole(dto.getRole());
    u.setName(dto.getName());
    u.setEmail(dto.getEmail());
    u.setContact(dto.getContact());

    // save happens automatically if managed
    return repo.save(u);
  }
}
