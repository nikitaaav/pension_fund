package ru.pfr.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import ru.pfr.model.Pensioner;
import ru.pfr.model.PensionPayment;
import ru.pfr.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Service class for communicating with the server's REST API.
 */
public class ApiService {
    private static final String BASE_URL = "http://localhost:8080/api";
    private final ObjectMapper objectMapper;
    private Long currentUserId;
    private String currentUsername;
    private String currentUserRole;

    public ApiService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        System.out.println("ApiService initialized with ObjectMapper configured for dates and unknown properties");
    }

    public void setCurrentUser(Long id, String username, String role) {
        this.currentUserId = id;
        this.currentUsername = username;
        this.currentUserRole = role;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(currentUserRole);
    }

    public Map<String, Object> login(User user) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/auth/login");
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(user)));

            try (CloseableHttpResponse response = client.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                if (response.getCode() == 200) {
                    return objectMapper.readValue(jsonResponse, new TypeReference<Map<String, Object>>() {});
                }
                throw new IOException(jsonResponse.isEmpty() ? response.getReasonPhrase() : jsonResponse);
            } catch (ParseException e) {
                throw new IOException("Ошибка при обработке ответа сервера", e);
            }
        }
    }

    public Map<String, Object> register(User user) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/auth/register");
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(user)));

            try (CloseableHttpResponse response = client.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                if (response.getCode() == 200) {
                    return objectMapper.readValue(jsonResponse, new TypeReference<Map<String, Object>>() {});
                }
                throw new IOException(jsonResponse.isEmpty() ? response.getReasonPhrase() : jsonResponse);
            } catch (ParseException e) {
                throw new IOException("Ошибка при обработке ответа сервера", e);
            }
        }
    }

    public List<User> getAllUsers() throws IOException {
        if (!isAdmin()) {
            throw new IOException("Недостаточно прав для выполнения операции");
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL + "/users");
            request.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getCode() == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    return objectMapper.readValue(jsonResponse, new TypeReference<List<User>>() {});
                }
                throw new IOException("Ошибка получения списка пользователей: " + response.getReasonPhrase());
            } catch (ParseException e) {
                throw new IOException("Ошибка при обработке ответа сервера", e);
            }
        }
    }

    public User createUser(User user) throws IOException {
        if (!isAdmin()) {
            throw new IOException("Недостаточно прав для выполнения операции");
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/users");
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(user)));

            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getCode() == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    return objectMapper.readValue(jsonResponse, User.class);
                }
                throw new IOException("Ошибка создания пользователя: " + response.getReasonPhrase());
            } catch (ParseException e) {
                throw new IOException("Ошибка при обработке ответа сервера", e);
            }
        }
    }

    public User updateUser(Long id, User user) throws IOException {
        if (!isAdmin()) {
            throw new IOException("Недостаточно прав для выполнения операции");
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(BASE_URL + "/users/" + id);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(user)));

            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getCode() == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    return objectMapper.readValue(jsonResponse, User.class);
                }
                throw new IOException("Ошибка обновления пользователя: " + response.getReasonPhrase());
            } catch (ParseException e) {
                throw new IOException("Ошибка при обработке ответа сервера", e);
            }
        }
    }

    public void deleteUser(Long id) throws IOException {
        if (!isAdmin()) {
            throw new IOException("Недостаточно прав для выполнения операции");
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpDelete request = new HttpDelete(BASE_URL + "/users/" + id);
            request.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getCode() != 200) {
                    throw new IOException("Ошибка удаления пользователя: " + response.getReasonPhrase());
                }
            }
        }
    }

    public List<Pensioner> getAllPensioners() {
        System.out.println("Getting all pensioners...");
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL + "/pensioners");
            return client.execute(request, response -> {
                if (response.getCode() != 200) {
                    throw new RuntimeException("Server returned code " + response.getCode());
                }
                List<Pensioner> pensioners = objectMapper.readValue(
                    response.getEntity().getContent(),
                    new TypeReference<List<Pensioner>>() {}
                );
                System.out.println("Received pensioners: " + pensioners);
                return pensioners;
            });
        } catch (IOException e) {
            System.err.println("Failed to get pensioners: " + e.getMessage());
            throw new RuntimeException("Failed to get pensioners", e);
        }
    }

    public Pensioner getPensionerById(Long id) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL + "/pensioners/" + id);
            return client.execute(request, response -> {
                return objectMapper.readValue(
                    response.getEntity().getContent(),
                    Pensioner.class
                );
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to get pensioner", e);
        }
    }

    public Pensioner createPensioner(Pensioner pensioner) {
        System.out.println("Creating pensioner: " + pensioner);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/pensioners");
            String jsonBody = objectMapper.writeValueAsString(pensioner);
            System.out.println("Request body: " + jsonBody);
            StringEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            request.setEntity(entity);
            
            return client.execute(request, response -> {
                if (response.getCode() != 200) {
                    throw new RuntimeException("Server returned code " + response.getCode());
                }
                Pensioner created = objectMapper.readValue(
                    response.getEntity().getContent(),
                    Pensioner.class
                );
                System.out.println("Created pensioner: " + created);
                return created;
            });
        } catch (IOException e) {
            System.err.println("Failed to create pensioner: " + e.getMessage());
            throw new RuntimeException("Failed to create pensioner", e);
        }
    }

    public Pensioner updatePensioner(Long id, Pensioner pensioner) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(BASE_URL + "/pensioners/" + id);
            StringEntity entity = new StringEntity(
                objectMapper.writeValueAsString(pensioner),
                ContentType.APPLICATION_JSON
            );
            request.setEntity(entity);
            return client.execute(request, response -> {
                return objectMapper.readValue(
                    response.getEntity().getContent(),
                    Pensioner.class
                );
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to update pensioner", e);
        }
    }

    public void deletePensioner(Long id) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpDelete request = new HttpDelete(BASE_URL + "/pensioners/" + id);
            client.execute(request, response -> null);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete pensioner", e);
        }
    }

    public List<PensionPayment> getAllPayments() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL + "/payments");
            return client.execute(request, response -> {
                return objectMapper.readValue(
                    response.getEntity().getContent(),
                    new TypeReference<List<PensionPayment>>() {}
                );
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to get payments", e);
        }
    }

    public PensionPayment getPaymentById(Long id) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL + "/payments/" + id);
            return client.execute(request, response -> {
                return objectMapper.readValue(
                    response.getEntity().getContent(),
                    PensionPayment.class
                );
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to get payment", e);
        }
    }

    public PensionPayment createPayment(PensionPayment payment) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/payments");
            StringEntity entity = new StringEntity(
                objectMapper.writeValueAsString(payment),
                ContentType.APPLICATION_JSON
            );
            request.setEntity(entity);
            return client.execute(request, response -> {
                return objectMapper.readValue(
                    response.getEntity().getContent(),
                    PensionPayment.class
                );
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to create payment", e);
        }
    }

    public PensionPayment updatePayment(Long id, PensionPayment payment) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(BASE_URL + "/payments/" + id);
            StringEntity entity = new StringEntity(
                objectMapper.writeValueAsString(payment),
                ContentType.APPLICATION_JSON
            );
            request.setEntity(entity);
            return client.execute(request, response -> {
                return objectMapper.readValue(
                    response.getEntity().getContent(),
                    PensionPayment.class
                );
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to update payment", e);
        }
    }

    public void deletePayment(Long id) throws IOException {
        System.out.println("Sending delete request for payment id: " + id);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpDelete request = new HttpDelete(BASE_URL + "/payments/" + id);
            client.execute(request, response -> {
                System.out.println("Delete response status: " + response.getCode());
                if (response.getCode() != 200) {
                    String errorMessage = new String(response.getEntity().getContent().readAllBytes());
                    System.out.println("Delete error response: " + errorMessage);
                    throw new RuntimeException("Failed to delete payment: " + errorMessage);
                }
                return null;
            });
        } catch (Exception e) {
            System.out.println("Error during payment deletion: " + e.getMessage());
            throw e;
        }
    }

    public Map<String, Object> getStatistics() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL + "/pensioners/statistics");
            return client.execute(request, response -> {
                return objectMapper.readValue(
                    response.getEntity().getContent(),
                    new TypeReference<Map<String, Object>>() {}
                );
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to get statistics", e);
        }
    }
} 