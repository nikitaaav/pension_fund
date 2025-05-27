package ru.pfr.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pfr.server.entity.PensionerEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Pensioner entity.
 * Provides methods for accessing pensioner data in the database.
 */
public interface PensionerRepository extends JpaRepository<PensionerEntity, Long> {
    
    List<PensionerEntity> findByLastNameContainingIgnoreCase(String lastName);
    
    List<PensionerEntity> findBySnils(String snils);
    
    @Query("SELECT p FROM PensionerEntity p WHERE p.birthDate BETWEEN :startDate AND :endDate")
    List<PensionerEntity> findByBirthDateBetween(@Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM PensionerEntity p WHERE p.basePensionAmount > :minAmount")
    List<PensionerEntity> findByBasePensionAmountGreaterThan(@Param("minAmount") Double minAmount);
} 