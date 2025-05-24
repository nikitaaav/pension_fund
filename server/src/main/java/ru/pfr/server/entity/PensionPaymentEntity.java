package ru.pfr.server.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * JPA entity representing a pension payment in the database.
 */
@Entity
@Table(name = "pension_payments")
@Data
@NoArgsConstructor
public class PensionPaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pensioner_id", nullable = false)
    private PensionerEntity pensioner;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String paymentType;

    @Column(nullable = false)
    private String status;

    private String description;
} 