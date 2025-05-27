package ru.pfr.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.pfr.client.service.ApiService;
import ru.pfr.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserDialogController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private CheckBox activeCheckBox;

    private Stage dialogStage;
    private User user;
    private boolean saveClicked = false;
    private ApiService apiService;
    private boolean isEditMode = false;

    @FXML
    private void initialize() {
        ObservableList<String> roles = FXCollections.observableArrayList("ADMIN", "USER");
        roleComboBox.setItems(roles);
        roleComboBox.getSelectionModel().selectFirst();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            isEditMode = true;
            usernameField.setText(user.getUsername());
            roleComboBox.setValue(user.getRole());
            activeCheckBox.setSelected(user.isActive());
            // Не заполняем пароль при редактировании
        }
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            if (user == null) {
                user = new User();
            }
            user.setUsername(usernameField.getText());
            if (!passwordField.getText().isEmpty()) {
                user.setPassword(passwordField.getText());
            }
            user.setRole(roleComboBox.getValue());
            user.setActive(activeCheckBox.isSelected());

            saveClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (usernameField.getText() == null || usernameField.getText().length() == 0) {
            errorMessage += "Имя пользователя не может быть пустым!\n";
        }
        if (!isEditMode && (passwordField.getText() == null || passwordField.getText().length() == 0)) {
            errorMessage += "Пароль не может быть пустым!\n";
        }
        if (roleComboBox.getValue() == null) {
            errorMessage += "Выберите роль!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Пожалуйста, исправьте следующие поля:");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }

    public User getUser() {
        return user;
    }
} 