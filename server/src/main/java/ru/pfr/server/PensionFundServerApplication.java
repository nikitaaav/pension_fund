package ru.pfr.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Pension Fund Server.
 * This class bootstraps the Spring Boot application.
 */
@SpringBootApplication
public class PensionFundServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PensionFundServerApplication.class, args);
    }
} 