package ru.pfr.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a pensioner in the pension fund system.
 * This is the parent entity in the two-level data structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pensioner {
    private Long id;
    private String lastName;
    private String firstName;
    private String middleName;
    private LocalDate birthDate;
    private String snils; // Social Security Number
    private String address;
    private String phoneNumber;
    private LocalDate pensionStartDate;
    private Double basePensionAmount;
    private List<PensionPayment> payments;
} 