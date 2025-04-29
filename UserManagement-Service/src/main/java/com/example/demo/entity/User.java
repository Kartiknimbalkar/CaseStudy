package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @NotBlank(message = "Username shouldn't be blank")
    private String username;
    @NotBlank(message = "Password shouldn't be blank")
    private String password;
    @NotBlank(message = "Role shouldn't be blank")
    private String role;     //  "ADMIN" or "DOCTOR"
}
