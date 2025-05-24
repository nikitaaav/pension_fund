package ru.pfr.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String role; // "ADMIN" или "USER"
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private boolean active;
} 