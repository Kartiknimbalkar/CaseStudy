package com.example.demo.entity;

import lombok.Data;

@Data
public class SignUpDto {
    private String username;
    private String password;
    private String role;
    private String name;
    private String email;
    private String contact;
}

