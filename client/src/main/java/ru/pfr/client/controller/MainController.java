package ru.pfr.client.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.pfr.client.service.ApiService;
import ru.pfr.model.Pensioner;
import ru.pfr.model.PensionPayment;
import ru.pfr.model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Main controller for the Pension Fund Client application.
 * Handles user interactions and manages the UI.
 */
public class MainController {

    private ApiService apiService;
    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
        postInit();
    }

    @FXML private TabPane tabPane;
    @FXML private Tab pensionersTab;
    @FXML private Tab paymentsTab;
    @FXML private Tab statisticsTab;
    @FXML private Tab usersTab;

    @FXML private TableView<Pensioner> pensionersTable;
    @FXML private TableColumn<Pensioner, String> lastNameColumn;
    @FXML private TableColumn<Pensioner, String> firstNameColumn;
    @FXML private TableColumn<Pensioner, String> middleNameColumn;
    @FXML private TableColumn<Pensioner, String> snilsColumn;
    @FXML private TableColumn<Pensioner, LocalDate> birthDateColumn;
    @FXML private TableColumn<Pensioner, String> addressColumn;
    @FXML private TableColumn<Pensioner, String> phoneColumn;
    @FXML private TableColumn<Pensioner, LocalDate> pensionStartDateColumn;
    @FXML private TableColumn<Pensioner, Double> basePensionAmountColumn;

    @FXML private TextField pensionerSearchField;
    @FXML private ComboBox<String> pensionerSearchType;
    @FXML private DatePicker paymentDateFrom;
    @FXML private DatePicker paymentDateTo;
    @FXML private TextField paymentAmountFrom;
    @FXML private TextField paymentAmountTo;
    @FXML private ComboBox<String> paymentTypeFilter;

    @FXML private TableView<PensionPayment> paymentsTable;
    @FXML private TableColumn<PensionPayment, String> pensionerColumn;
    @FXML private TableColumn<PensionPayment, LocalDate> paymentDateColumn;
    @FXML private TableColumn<PensionPayment, Double> amountColumn;
    @FXML private TableColumn<PensionPayment, String> paymentTypeColumn;
    @FXML private TableColumn<PensionPayment, String> statusColumn;
    @FXML private TableColumn<PensionPayment, String> descriptionColumn;

    @FXML private Label totalPensionersLabel;
    @FXML private Label averagePensionAmountLabel;
    @FXML private Label maxPensionAmountLabel;
    @FXML private Label minPensionAmountLabel;

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Long> userIdColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> createdAtColumn;
    @FXML private TableColumn<User, String> lastLoginAtColumn;
    @FXML private TableColumn<User, Boolean> activeColumn;
    @FXML private Button addUserButton;
    @FXML private Button editUserButton;
    @FXML private Button deleteUserButton;

    private final ObservableList<Pensioner> pensioners = FXCollections.observableArrayList();
    private final ObservableList<PensionPayment> payments = FXCollections.observableArrayList();
    private final ObservableList<User> users = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupPensionersTable();
        setupPaymentsTable();
        setupFilters();
        setupUsersTable();
    }

    private void postInit() {
        if (!apiService.isAdmin()) {
            tabPane.getTabs().remove(usersTab);
        } else {
            refreshUsers();
        }
        refreshData();
    }

    private void setupPensionersTable() {
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        middleNameColumn.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        snilsColumn.setCellValueFactory(new PropertyValueFactory<>("snils"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        pensionStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("pensionStartDate"));
        basePensionAmountColumn.setCellValueFactory(new PropertyValueFactory<>("basePensionAmount"));

        pensionersTable.setItems(pensioners);
    }

    private void setupPaymentsTable() {
        pensionerColumn.setCellValueFactory(cellData -> {
            PensionPayment payment = cellData.getValue();
            if (payment != null && payment.getPensionerId() != null) {
                // Ищем пенсионера в текущем списке пенсионеров (это быстрее, чем делать запрос к серверу)
                Pensioner pensioner = pensioners.stream()
                    .filter(p -> p.getId().equals(payment.getPensionerId()))
                    .findFirst()
                    .orElse(null);
                
                if (pensioner != null) {
                    return new javafx.beans.property.SimpleStringProperty(
                        pensioner.getLastName() + " " + pensioner.getFirstName() + " " + pensioner.getMiddleName()
                    );
                }
            }
            return new javafx.beans.property.SimpleStringProperty("Не найден");
        });

        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("paymentType"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        paymentsTable.setItems(payments);
    }

    private void setupFilters() {
        // Настройка фильтров для пенсионеров
        pensionerSearchType.getItems().addAll(
            "ФИО",
            "СНИЛС",
            "Адрес",
            "Телефон"
        );
        pensionerSearchType.setValue("ФИО");
        
        // Настройка фильтров для платежей
        paymentTypeFilter.getItems().addAll(
            "Все типы",
            "Ежемесячная выплата",
            "Единовременная выплата",
            "Социальная доплата"
        );
        paymentTypeFilter.setValue("Все типы");
        
        // Добавляем слушатели для фильтров
        pensionerSearchField.textProperty().addListener((obs, oldVal, newVal) -> filterPensioners());
        pensionerSearchType.valueProperty().addListener((obs, oldVal, newVal) -> filterPensioners());
        
        paymentDateFrom.valueProperty().addListener((obs, oldVal, newVal) -> filterPayments());
        paymentDateTo.valueProperty().addListener((obs, oldVal, newVal) -> filterPayments());
        paymentAmountFrom.textProperty().addListener((obs, oldVal, newVal) -> filterPayments());
        paymentAmountTo.textProperty().addListener((obs, oldVal, newVal) -> filterPayments());
        paymentTypeFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterPayments());
    }

    private void filterPensioners() {
        String searchText = pensionerSearchField.getText().toLowerCase();
        String searchType = pensionerSearchType.getValue();
        
        List<Pensioner> filteredList = pensioners.stream()
            .filter(pensioner -> {
                if (searchText.isEmpty()) return true;
                
                switch (searchType) {
                    case "ФИО":
                        return (pensioner.getLastName() + " " + pensioner.getFirstName() + " " + pensioner.getMiddleName())
                            .toLowerCase().contains(searchText);
                    case "СНИЛС":
                        return pensioner.getSnils().toLowerCase().contains(searchText);
                    case "Адрес":
                        return pensioner.getAddress().toLowerCase().contains(searchText);
                    case "Телефон":
                        return pensioner.getPhoneNumber().toLowerCase().contains(searchText);
                    default:
                        return true;
                }
            })
            .collect(Collectors.toList());
        
        pensionersTable.setItems(FXCollections.observableArrayList(filteredList));
    }

    private void filterPayments() {
        LocalDate fromDate = paymentDateFrom.getValue();
        LocalDate toDate = paymentDateTo.getValue();
        String amountFrom = paymentAmountFrom.getText();
        String amountTo = paymentAmountTo.getText();
        String paymentType = paymentTypeFilter.getValue();
        
        List<PensionPayment> filteredList = payments.stream()
            .filter(payment -> {
                // Фильтр по дате
                if (fromDate != null && payment.getPaymentDate().isBefore(fromDate)) return false;
                if (toDate != null && payment.getPaymentDate().isAfter(toDate)) return false;
                
                // Фильтр по сумме
                if (!amountFrom.isEmpty()) {
                    try {
                        double minAmount = Double.parseDouble(amountFrom);
                        if (payment.getAmount() < minAmount) return false;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
                if (!amountTo.isEmpty()) {
                    try {
                        double maxAmount = Double.parseDouble(amountTo);
                        if (payment.getAmount() > maxAmount) return false;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
                
                // Фильтр по типу платежа
                if (!paymentType.equals("Все типы") && !payment.getPaymentType().equals(paymentType)) {
                    return false;
                }
                
                return true;
            })
            .collect(Collectors.toList());
        
        paymentsTable.setItems(FXCollections.observableArrayList(filteredList));
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void showPensionersView() {
        tabPane.getSelectionModel().select(pensionersTab);
    }

    @FXML
    private void showPaymentsView() {
        tabPane.getSelectionModel().select(paymentsTab);
    }

    @FXML
    private void showStatisticsView() {
        tabPane.getSelectionModel().select(statisticsTab);
        refreshStatistics();
    }

    @FXML
    private void handleAddPensioner() {
        System.out.println("Adding new pensioner...");
        Pensioner pensioner = new Pensioner();
        if (showPensionerDialog(pensioner, "Добавление пенсионера")) {
            System.out.println("Dialog confirmed, creating pensioner: " + pensioner);
            try {
                Pensioner newPensioner = apiService.createPensioner(pensioner);
                System.out.println("Pensioner created successfully: " + newPensioner);
                pensioners.add(newPensioner);
            } catch (Exception e) {
                System.err.println("Error creating pensioner: " + e.getMessage());
                e.printStackTrace();
                showAlert("Ошибка", "Ошибка при создании пенсионера", 
                    "Произошла ошибка при сохранении данных: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleEditPensioner() {
        Pensioner selected = pensionersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (showPensionerDialog(selected, "Редактирование пенсионера")) {
                try {
                    Pensioner updatedPensioner = apiService.updatePensioner(selected.getId(), selected);
                    int index = pensioners.indexOf(selected);
                    if (index >= 0) {
                        pensioners.set(index, updatedPensioner);
                        pensionersTable.refresh();
                    }
                } catch (Exception e) {
                    System.err.println("Error updating pensioner: " + e.getMessage());
                    e.printStackTrace();
                    showAlert("Ошибка", "Ошибка при обновлении пенсионера", 
                        "Произошла ошибка при сохранении данных: " + e.getMessage());
                }
            }
        } else {
            showAlert("Ошибка", "Не выбран пенсионер", "Пожалуйста, выберите пенсионера для редактирования.");
        }
    }

    @FXML
    private void handleDeletePensioner() {
        Pensioner selected = pensionersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Подтверждение удаления");
            confirmDialog.setHeaderText("Удаление пенсионера");
            confirmDialog.setContentText("Вы действительно хотите удалить этого пенсионера? Все его платежи также будут удалены.");

            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        apiService.deletePensioner(selected.getId());
                        pensioners.remove(selected);
                        pensionersTable.refresh();
                        // Не обновляем платежи автоматически, чтобы избежать повторного появления удаленных записей
                    } catch (Exception e) {
                        System.err.println("Error deleting pensioner: " + e.getMessage());
                        showAlert("Ошибка", "Ошибка при удалении пенсионера", 
                            "Произошла ошибка при удалении данных: " + e.getMessage());
                    }
                }
            });
        }
    }

    @FXML
    private void handleAddPayment() {
        try {
            System.out.println("Adding new payment...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/payment_dialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Добавление платежа");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(paymentsTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            PaymentDialogController controller = loader.getController();
            controller.setPayment(null); // Pass null to create a new payment

            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                PensionPayment newPayment = controller.getPayment();
                System.out.println("Dialog confirmed, creating payment: " + newPayment);
                try {
                    PensionPayment createdPayment = apiService.createPayment(newPayment);
                    System.out.println("Payment created successfully: " + createdPayment);
                    payments.add(createdPayment);
                } catch (Exception e) {
                    System.err.println("Error creating payment: " + e.getMessage());
                    e.printStackTrace();
                    showAlert("Ошибка", "Ошибка при создании платежа", 
                        "Произошла ошибка при сохранении данных: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при открытии диалога", 
                "Не удалось открыть диалог добавления платежа: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditPayment() {
        PensionPayment selected = paymentsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/payment_dialog.fxml"));
                Parent root = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Редактирование платежа");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(paymentsTable.getScene().getWindow());
                dialogStage.setScene(new Scene(root));

                PaymentDialogController controller = loader.getController();
                // Создаем копию платежа для редактирования
                PensionPayment paymentCopy = new PensionPayment();
                paymentCopy.setId(selected.getId());
                paymentCopy.setPensionerId(selected.getPensionerId());
                paymentCopy.setPaymentDate(selected.getPaymentDate());
                paymentCopy.setAmount(selected.getAmount());
                paymentCopy.setPaymentType(selected.getPaymentType());
                paymentCopy.setStatus(selected.getStatus());
                paymentCopy.setDescription(selected.getDescription());
                
                controller.setPayment(paymentCopy);

                dialogStage.showAndWait();

                if (controller.isOkClicked()) {
                    PensionPayment updatedPayment = controller.getPayment();
                    PensionPayment savedPayment = apiService.updatePayment(selected.getId(), updatedPayment);
                    int index = payments.indexOf(selected);
                    if (index >= 0) {
                        payments.set(index, savedPayment);
                        paymentsTable.refresh();
                    }
                }
            } catch (Exception e) {
                System.err.println("Error editing payment: " + e.getMessage());
                showAlert("Ошибка", "Ошибка при редактировании платежа", 
                    "Произошла ошибка при редактировании данных: " + e.getMessage());
            }
        } else {
            showAlert("Ошибка", "Не выбран платеж", "Пожалуйста, выберите платеж для редактирования.");
        }
    }

    @FXML
    private void handleDeletePayment() {
        PensionPayment selectedPayment = paymentsTable.getSelectionModel().getSelectedItem();
        if (selectedPayment != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление платежа");
            alert.setContentText("Вы уверены, что хотите удалить выбранный платеж?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    System.out.println("Attempting to delete payment: " + selectedPayment.getId());
                    apiService.deletePayment(selectedPayment.getId());
                    System.out.println("Payment deleted successfully");
                    
                    // Удаляем из ObservableList
                    payments.remove(selectedPayment);
                    System.out.println("Payment removed from local list");
                    
                    // Обновляем таблицу
                    paymentsTable.refresh();
                    System.out.println("Table refreshed");
                    
                    // Обновляем все данные
                    refreshData();
                    System.out.println("All data refreshed");
                } catch (Exception e) {
                    System.err.println("Error during payment deletion: " + e.getMessage());
                    showError("Ошибка при удалении платежа", e.getMessage());
                }
            }
        } else {
            showError("Ошибка", "Пожалуйста, выберите платеж для удаления");
        }
    }

    @FXML
    private void handleRefreshPensioners() {
        refreshPensioners();
    }

    @FXML
    private void handleRefreshPayments() {
        refreshPayments();
    }

    @FXML
    private void handleRefreshStatistics() {
        refreshStatistics();
    }

    @FXML
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setHeaderText("Информационно-справочная система Пенсионного фонда РФ");
        alert.setContentText("Версия 1.0\n\nАвтор: Волков Никита\nГруппа: ТРПО23-3\n\nКросс-платформенное приложение для учета и управления данными пенсионеров и пенсионных выплат.");
        alert.showAndWait();
    }

    @FXML
    private void handleClearPensionerFilters() {
        pensionerSearchField.clear();
        pensionerSearchType.setValue("ФИО");
        filterPensioners();
    }

    @FXML
    private void handleClearPaymentFilters() {
        paymentDateFrom.setValue(null);
        paymentDateTo.setValue(null);
        paymentAmountFrom.clear();
        paymentAmountTo.clear();
        paymentTypeFilter.setValue("Все типы");
        filterPayments();
    }

    private void refreshData() {
        Platform.runLater(() -> {
            refreshPensioners();
            refreshPayments();
            refreshStatistics();
        });
    }

    private void refreshPensioners() {
        try {
            List<Pensioner> allPensioners = apiService.getAllPensioners();
            pensioners.clear();
            pensioners.addAll(allPensioners);
            pensionersTable.refresh();
        } catch (Exception e) {
            System.err.println("Error refreshing pensioners: " + e.getMessage());
            showAlert("Ошибка", "Ошибка при обновлении данных", 
                "Произошла ошибка при получении списка пенсионеров: " + e.getMessage());
        }
    }

    private void refreshPayments() {
        try {
            List<PensionPayment> allPayments = apiService.getAllPayments();
            payments.clear();
            payments.addAll(allPayments);
            paymentsTable.refresh();
        } catch (Exception e) {
            System.err.println("Error refreshing payments: " + e.getMessage());
            showAlert("Ошибка", "Ошибка при обновлении данных", 
                "Произошла ошибка при получении списка платежей: " + e.getMessage());
        }
    }

    private void refreshStatistics() {
        Map<String, Object> stats = apiService.getStatistics();
        totalPensionersLabel.setText("Всего пенсионеров: " + stats.get("totalPensioners"));
        averagePensionAmountLabel.setText("Средний размер пенсии: " + stats.get("averagePensionAmount"));
        maxPensionAmountLabel.setText("Максимальный размер пенсии: " + stats.get("maxPensionAmount"));
        minPensionAmountLabel.setText("Минимальный размер пенсии: " + stats.get("minPensionAmount"));
    }

    private boolean showPensionerDialog(Pensioner pensioner, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pensioner_dialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(pensionersTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            PensionerDialogController controller = loader.getController();
            controller.setPensioner(pensioner);

            dialogStage.showAndWait();
            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean showPaymentDialog(PensionPayment payment, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/payment_dialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(paymentsTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            PaymentDialogController controller = loader.getController();
            controller.setPayment(payment);

            dialogStage.showAndWait();
            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void setupUsersTable() {
        if (usersTable == null) return;
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        createdAtColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getCreatedAt() != null ? cellData.getValue().getCreatedAt().toString() : ""
        ));
        lastLoginAtColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getLastLoginAt() != null ? cellData.getValue().getLastLoginAt().toString() : ""
        ));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        usersTable.setItems(users);
    }

    private void refreshUsers() {
        try {
            users.setAll(apiService.getAllUsers());
        } catch (Exception e) {
            showError("Ошибка при обновлении пользователей", e.getMessage());
        }
    }

    @FXML
    private void handleAddUser() {
        showError("Добавление пользователя", "Форма добавления пользователя не реализована");
    }

    @FXML
    private void handleEditUser() {
        showError("Изменение пользователя", "Форма редактирования пользователя не реализована");
    }

    @FXML
    private void handleDeleteUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Удаление пользователя", "Выберите пользователя для удаления");
            return;
        }
        try {
            apiService.deleteUser(selected.getId());
            refreshUsers();
        } catch (Exception e) {
            showError("Ошибка удаления пользователя", e.getMessage());
        }
    }
} 