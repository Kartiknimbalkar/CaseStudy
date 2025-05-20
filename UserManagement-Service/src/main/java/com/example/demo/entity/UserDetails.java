package com.example.demo.entity;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
public class UserDetails {
    private String username;
    private String role;
    private String name;
    private String email;
    private String contact;

    public UserDetails(User user) {
        this.username = user.getUsername();
        this.role = user.getRole();
        this.name = user.getName();
        this.email = user.getEmail();
        this.contact = user.getContact();
    }
}
