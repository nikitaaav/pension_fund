package ru.pfr.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pfr.model.Pensioner;
import ru.pfr.server.entity.PensionerEntity;
import ru.pfr.server.repository.PensionerRepository;
import ru.pfr.server.repository.PensionPaymentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing pensioners.
 */
@Service
@Transactional
public class PensionerService {

    private final PensionerRepository pensionerRepository;
    private final PensionPaymentRepository paymentRepository;
    @Autowired
    private EntityManager entityManager;

    @Autowired
    public PensionerService(PensionerRepository pensionerRepository, 
                          PensionPaymentRepository paymentRepository) {
        this.pensionerRepository = pensionerRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<Pensioner> getAllPensioners() {
        return pensionerRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<Pensioner> getPensionerById(Long id) {
        return pensionerRepository.findById(id)
                .map(this::convertToDto);
    }

    public Pensioner createPensioner(Pensioner pensioner) {
        PensionerEntity entity = convertToEntity(pensioner);
        return convertToDto(pensionerRepository.save(entity));
    }

    public Pensioner updatePensioner(Long id, Pensioner pensioner) {
        PensionerEntity existingEntity = pensionerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pensioner not found"));
        
        updateEntity(existingEntity, pensioner);
        return convertToDto(pensionerRepository.save(existingEntity));
    }

    public void deletePensioner(Long id) {
        // First delete all payments for this pensioner
        Query deletePaymentsQuery = entityManager.createNativeQuery(
            "DELETE FROM pension_payments WHERE pensioner_id = :pensionerId");
        deletePaymentsQuery.setParameter("pensionerId", id);
        deletePaymentsQuery.executeUpdate();

        // Then delete the pensioner
        Query deletePensionerQuery = entityManager.createNativeQuery(
            "DELETE FROM pensioners WHERE id = :id");
        deletePensionerQuery.setParameter("id", id);
        deletePensionerQuery.executeUpdate();
    }

    public List<Pensioner> searchPensioners(String lastName, String snils, 
                                          LocalDate startDate, LocalDate endDate) {
        if (lastName != null) {
            return pensionerRepository.findByLastNameContainingIgnoreCase(lastName).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
        if (snils != null) {
            return pensionerRepository.findBySnils(snils).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
        if (startDate != null && endDate != null) {
            return pensionerRepository.findByBirthDateBetween(startDate, endDate).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
        return getAllPensioners();
    }

    public Map<String, Object> getStatistics() {
        List<PensionerEntity> allPensioners = pensionerRepository.findAll();
        
        return Map.of(
            "totalPensioners", allPensioners.size(),
            "averagePensionAmount", allPensioners.stream()
                    .mapToDouble(PensionerEntity::getBasePensionAmount)
                    .average()
                    .orElse(0.0),
            "maxPensionAmount", allPensioners.stream()
                    .mapToDouble(PensionerEntity::getBasePensionAmount)
                    .max()
                    .orElse(0.0),
            "minPensionAmount", allPensioners.stream()
                    .mapToDouble(PensionerEntity::getBasePensionAmount)
                    .min()
                    .orElse(0.0)
        );
    }

    private Pensioner convertToDto(PensionerEntity entity) {
        Pensioner dto = new Pensioner();
        dto.setId(entity.getId());
        dto.setLastName(entity.getLastName());
        dto.setFirstName(entity.getFirstName());
        dto.setMiddleName(entity.getMiddleName());
        dto.setBirthDate(entity.getBirthDate());
        dto.setSnils(entity.getSnils());
        dto.setAddress(entity.getAddress());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setPensionStartDate(entity.getPensionStartDate());
        dto.setBasePensionAmount(entity.getBasePensionAmount());
        return dto;
    }

    private PensionerEntity convertToEntity(Pensioner dto) {
        PensionerEntity entity = new PensionerEntity();
        updateEntity(entity, dto);
        return entity;
    }

    private void updateEntity(PensionerEntity entity, Pensioner dto) {
        entity.setLastName(dto.getLastName());
        entity.setFirstName(dto.getFirstName());
        entity.setMiddleName(dto.getMiddleName());
        entity.setBirthDate(dto.getBirthDate());
        entity.setSnils(dto.getSnils());
        entity.setAddress(dto.getAddress());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setPensionStartDate(dto.getPensionStartDate());
        entity.setBasePensionAmount(dto.getBasePensionAmount());
    }
} 