package ru.pfr.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pfr.model.User;
import ru.pfr.server.entity.UserEntity;
import ru.pfr.server.service.ActionLogService;
import ru.pfr.server.service.UserService;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final ActionLogService actionLogService;

    @Autowired
    public AuthController(UserService userService, ActionLogService actionLogService) {
        this.userService = userService;
        this.actionLogService = actionLogService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            if (userService.validateUser(user.getUsername(), user.getPassword())) {
                UserEntity userEntity = userService.findByUsername(user.getUsername()).orElseThrow();
                userService.updateLastLogin(user.getUsername());
                actionLogService.logAction(
                    userEntity.getId(),
                    userEntity.getUsername(),
                    "LOGIN",
                    "Успешный вход в систему"
                );
                Map<String, Object> response = new HashMap<>();
                response.put("id", userEntity.getId());
                response.put("username", userEntity.getUsername());
                response.put("role", userEntity.getRole());
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body("Неверное имя пользователя или пароль");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Login error: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            UserEntity newUser = userService.createUser(
                user.getUsername(),
                user.getPassword(),
                "USER" // По умолчанию создаем обычного пользователя
            );
            actionLogService.logAction(
                newUser.getId(),
                newUser.getUsername(),
                "REGISTER",
                "Регистрация нового пользователя"
            );
            Map<String, Object> response = new HashMap<>();
            response.put("id", newUser.getId());
            response.put("username", newUser.getUsername());
            response.put("role", newUser.getRole());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Registration error: " + e.getMessage());
        }
    }
} 