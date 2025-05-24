package ru.pfr.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * Represents a pension payment made to a pensioner.
 * This is the child entity in the two-level data structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PensionPayment {
    private Long id;
    private Long pensionerId;
    private LocalDate paymentDate;
    private Double amount;
    private String paymentType; // Regular, Additional, One-time
    private String status; // Paid, Pending, Cancelled
    private String description;
} 