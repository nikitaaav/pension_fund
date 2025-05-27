package ru.pfr.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import ru.pfr.model.PensionPayment;
import ru.pfr.model.Pensioner;
import ru.pfr.client.service.ApiService;
import javafx.collections.FXCollections;

/**
 * Controller for the payment dialog window.
 */
public class PaymentDialogController {
    @FXML private ComboBox<Pensioner> pensionerComboBox;
    @FXML private DatePicker paymentDatePicker;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> paymentTypeComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField descriptionField;

    private PensionPayment payment;
    private boolean okClicked = false;
    private final ApiService apiService = new ApiService();

    @FXML
    private void initialize() {
        System.out.println("PaymentDialogController initialized");
        
        // Initialize payment types
        paymentTypeComboBox.setItems(FXCollections.observableArrayList(
            "Базовая пенсия",
            "Надбавка",
            "Компенсация",
            "Единовременная выплата"
        ));

        // Initialize statuses
        statusComboBox.setItems(FXCollections.observableArrayList(
            "Обработка",
            "Выполнен",
            "Отменен"
        ));

        // Load pensioners into combo box
        try {
            pensionerComboBox.setItems(FXCollections.observableArrayList(apiService.getAllPensioners()));
            pensionerComboBox.setConverter(new javafx.util.StringConverter<Pensioner>() {
                @Override
                public String toString(Pensioner pensioner) {
                    return pensioner == null ? "" : pensioner.getLastName() + " " + 
                           pensioner.getFirstName() + " " + pensioner.getMiddleName();
                }

                @Override
                public Pensioner fromString(String string) {
                    return null; // Not needed for this use case
                }
            });
        } catch (Exception e) {
            System.err.println("Error loading pensioners: " + e.getMessage());
        }
    }

    public void setPayment(PensionPayment payment) {
        System.out.println("Setting payment: " + payment);
        if (payment == null) {
            this.payment = new PensionPayment();
        } else {
            this.payment = payment;
            if (payment.getId() != null) {
                // Editing existing payment
                Pensioner pensioner = apiService.getPensionerById(payment.getPensionerId());
                pensionerComboBox.setValue(pensioner);
                paymentDatePicker.setValue(payment.getPaymentDate());
                amountField.setText(String.valueOf(payment.getAmount()));
                paymentTypeComboBox.setValue(payment.getPaymentType());
                statusComboBox.setValue(payment.getStatus());
                descriptionField.setText(payment.getDescription());
            }
        }
    }

    public PensionPayment getPayment() {
        return payment;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            System.out.println("Saving payment data...");
            payment.setPensionerId(pensionerComboBox.getValue().getId());
            payment.setPaymentDate(paymentDatePicker.getValue());
            payment.setAmount(Double.parseDouble(amountField.getText()));
            payment.setPaymentType(paymentTypeComboBox.getValue());
            payment.setStatus(statusComboBox.getValue());
            payment.setDescription(descriptionField.getText());

            System.out.println("Saved payment: " + payment);
            okClicked = true;
            pensionerComboBox.getScene().getWindow().hide();
        }
    }

    @FXML
    private void handleCancel() {
        pensionerComboBox.getScene().getWindow().hide();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (pensionerComboBox.getValue() == null) {
            errorMessage += "Не выбран пенсионер!\n";
        }
        if (paymentDatePicker.getValue() == null) {
            errorMessage += "Не указана дата платежа!\n";
        }
        if (amountField.getText() == null || amountField.getText().length() == 0) {
            errorMessage += "Не указана сумма!\n";
        } else {
            try {
                Double.parseDouble(amountField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Неверный формат суммы!\n";
            }
        }
        if (paymentTypeComboBox.getValue() == null) {
            errorMessage += "Не указан тип платежа!\n";
        }
        if (statusComboBox.getValue() == null) {
            errorMessage += "Не указан статус платежа!\n";
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