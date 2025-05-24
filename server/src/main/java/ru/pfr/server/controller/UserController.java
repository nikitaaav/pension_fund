package ru.pfr.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pfr.model.User;
import ru.pfr.server.entity.UserEntity;
import ru.pfr.server.service.ActionLogService;
import ru.pfr.server.service.UserService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ActionLogService actionLogService;

    @Autowired
    public UserController(UserService userService, ActionLogService actionLogService) {
        this.userService = userService;
        this.actionLogService = actionLogService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers().stream()
            .map(this::convertToUser)
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            UserEntity newUser = userService.createUser(
                user.getUsername(),
                user.getPassword(),
                user.getRole()
            );
            
            actionLogService.logAction(
                newUser.getId(),
                newUser.getUsername(),
                "CREATE_USER",
                "Создан новый пользователь: " + newUser.getUsername()
            );
            
            return ResponseEntity.ok(convertToUser(newUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            UserEntity existingUser = userService.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            existingUser.setRole(user.getRole());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(user.getPassword());
            }
            
            userService.updateUser(existingUser);
            
            actionLogService.logAction(
                existingUser.getId(),
                existingUser.getUsername(),
                "UPDATE_USER",
                "Обновлен пользователь: " + existingUser.getUsername()
            );
            
            return ResponseEntity.ok(convertToUser(existingUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            UserEntity user = userService.getAllUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            userService.deleteUser(id);
            
            actionLogService.logAction(
                user.getId(),
                user.getUsername(),
                "DELETE_USER",
                "Удален пользователь: " + user.getUsername()
            );
            
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private User convertToUser(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setRole(entity.getRole());
        user.setCreatedAt(entity.getCreatedAt());
        user.setLastLoginAt(entity.getLastLoginAt());
        user.setActive(entity.isActive());
        return user;
    }
} 