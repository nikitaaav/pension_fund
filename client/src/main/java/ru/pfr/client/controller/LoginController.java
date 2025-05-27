package ru.pfr.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.pfr.client.service.ApiService;
import ru.pfr.model.User;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button registerButton;

    private final ApiService apiService = new ApiService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Пожалуйста, заполните все поля");
            return;
        }

        try {
            User user = new User(null, username, password, null, null, null, true);
            Map<String, Object> response = apiService.login(user);
            
            // Сохраняем информацию о пользователе
            Long userId = Long.valueOf(response.get("id").toString());
            String userRole = (String) response.get("role");
            apiService.setCurrentUser(userId, username, userRole);

            // Открываем главное окно
            openMainWindow();
        } catch (IOException e) {
            showError("Ошибка авторизации: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Пожалуйста, заполните все поля");
            return;
        }

        try {
            User user = new User(null, username, password, "USER", null, null, true);
            Map<String, Object> response = apiService.register(user);
            
            // После успешной регистрации автоматически входим
            Long userId = Long.valueOf(response.get("id").toString());
            String userRole = (String) response.get("role");
            apiService.setCurrentUser(userId, username, userRole);

            // Открываем главное окно
            openMainWindow();
        } catch (IOException e) {
            showError("Ошибка регистрации: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            
            // Передаем apiService в MainController
            MainController mainController = loader.getController();
            mainController.setApiService(apiService);
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Пенсионный фонд");
            stage.show();
        } catch (IOException e) {
            showError("Ошибка при открытии главного окна: " + e.getMessage());
        }
    }
} 