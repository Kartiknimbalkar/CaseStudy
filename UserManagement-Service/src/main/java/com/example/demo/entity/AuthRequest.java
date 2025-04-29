package com.example.demo.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
	@NotBlank(message = "Username cannot be Blank")
    private String username;
	@NotBlank(message = "Password cannot be Blank")
    private String password;
}
