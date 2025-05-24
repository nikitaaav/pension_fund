package ru.pfr.server.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * JPA entity representing a pensioner in the database.
 */
@Entity
@Table(name = "pensioners")
@Data
@NoArgsConstructor
public class PensionerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String firstName;

    private String middleName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, unique = true)
    private String snils;

    @Column(nullable = false)
    private String address;

    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate pensionStartDate;

    @Column(nullable = false)
    private Double basePensionAmount;

    @OneToMany(mappedBy = "pensioner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PensionPaymentEntity> payments;
} 