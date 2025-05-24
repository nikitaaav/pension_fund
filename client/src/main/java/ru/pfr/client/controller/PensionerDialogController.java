package ru.pfr.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import ru.pfr.model.Pensioner;

/**
 * Controller for the pensioner dialog window.
 */
public class PensionerDialogController {
    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField middleNameField;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField snilsField;
    @FXML private TextField addressField;
    @FXML private TextField phoneNumberField;
    @FXML private DatePicker pensionStartDatePicker;
    @FXML private TextField basePensionAmountField;

    private Pensioner pensioner;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        System.out.println("PensionerDialogController initialized");
    }

    public void setPensioner(Pensioner pensioner) {
        System.out.println("Setting pensioner: " + pensioner);
        this.pensioner = pensioner;

        if (pensioner.getLastName() != null) {
            lastNameField.setText(pensioner.getLastName());
            firstNameField.setText(pensioner.getFirstName());
            middleNameField.setText(pensioner.getMiddleName());
            birthDatePicker.setValue(pensioner.getBirthDate());
            snilsField.setText(pensioner.getSnils());
            addressField.setText(pensioner.getAddress());
            phoneNumberField.setText(pensioner.getPhoneNumber());
            pensionStartDatePicker.setValue(pensioner.getPensionStartDate());
            basePensionAmountField.setText(String.valueOf(pensioner.getBasePensionAmount()));
        }
    }

    public Pensioner getPensioner() {
        return pensioner;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            System.out.println("Saving pensioner data...");
            pensioner.setLastName(lastNameField.getText());
            pensioner.setFirstName(firstNameField.getText());
            pensioner.setMiddleName(middleNameField.getText());
            pensioner.setBirthDate(birthDatePicker.getValue());
            pensioner.setSnils(snilsField.getText());
            pensioner.setAddress(addressField.getText());
            pensioner.setPhoneNumber(phoneNumberField.getText());
            pensioner.setPensionStartDate(pensionStartDatePicker.getValue());
            pensioner.setBasePensionAmount(Double.parseDouble(basePensionAmountField.getText()));

            System.out.println("Saved pensioner: " + pensioner);
            okClicked = true;
            lastNameField.getScene().getWindow().hide();
        }
    }

    @FXML
    private void handleCancel() {
        lastNameField.getScene().getWindow().hide();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (lastNameField.getText() == null || lastNameField.getText().length() == 0) {
            errorMessage += "Не указана фамилия!\n";
        }
        if (firstNameField.getText() == null || firstNameField.getText().length() == 0) {
            errorMessage += "Не указано имя!\n";
        }
        if (birthDatePicker.getValue() == null) {
            errorMessage += "Не указана дата рождения!\n";
        }
        if (snilsField.getText() == null || !snilsField.getText().matches("\\d{3}-\\d{3}-\\d{3} \\d{2}")) {
            errorMessage += "Неверный формат СНИЛС (должен быть: XXX-XXX-XXX YY)!\n";
        }
        if (addressField.getText() == null || addressField.getText().length() == 0) {
            errorMessage += "Не указан адрес!\n";
        }
        if (pensionStartDatePicker.getValue() == null) {
            errorMessage += "Не указана дата начала выплаты пенсии!\n";
        }
        if (basePensionAmountField.getText() == null || basePensionAmountField.getText().length() == 0) {
            errorMessage += "Не указан базовый размер пенсии!\n";
        } else {
            try {
                Double.parseDouble(basePensionAmountField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Неверный формат базового размера пенсии!\n";
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            System.out.println("Validation errors: " + errorMessage);
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Пожалуйста, исправьте неверно заполненные поля");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
} 