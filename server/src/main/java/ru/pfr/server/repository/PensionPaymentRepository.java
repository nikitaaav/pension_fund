package ru.pfr.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pfr.server.entity.PensionPaymentEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for PensionPayment entity.
 * Provides methods for accessing pension payment data in the database.
 */
public interface PensionPaymentRepository extends JpaRepository<PensionPaymentEntity, Long> {
    
    List<PensionPaymentEntity> findByPensionerId(Long pensionerId);
    
    List<PensionPaymentEntity> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT p FROM PensionPaymentEntity p WHERE p.pensioner.id = :pensionerId AND p.paymentDate BETWEEN :startDate AND :endDate")
    List<PensionPaymentEntity> findByPensionerIdAndPaymentDateBetween(
            @Param("pensionerId") Long pensionerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(p.amount) FROM PensionPaymentEntity p WHERE p.pensioner.id = :pensionerId")
    Double getTotalPaymentsByPensionerId(@Param("pensionerId") Long pensionerId);
} 