package ru.pfr.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pfr.model.PensionPayment;
import ru.pfr.server.entity.PensionPaymentEntity;
import ru.pfr.server.entity.PensionerEntity;
import ru.pfr.server.repository.PensionPaymentRepository;
import ru.pfr.server.repository.PensionerRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing pension payments.
 */
@Service
@Transactional
public class PensionPaymentService {

    private final PensionPaymentRepository paymentRepository;
    private final PensionerRepository pensionerRepository;
    @Autowired
    private EntityManager entityManager;

    @Autowired
    public PensionPaymentService(PensionPaymentRepository paymentRepository,
                               PensionerRepository pensionerRepository) {
        this.paymentRepository = paymentRepository;
        this.pensionerRepository = pensionerRepository;
    }

    public List<PensionPayment> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<PensionPayment> getPaymentsByPensionerId(Long pensionerId) {
        return paymentRepository.findByPensionerId(pensionerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<PensionPayment> getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(this::convertToDto);
    }

    public PensionPayment createPayment(PensionPayment payment) {
        PensionPaymentEntity entity = convertToEntity(payment);
        return convertToDto(paymentRepository.save(entity));
    }

    public PensionPayment updatePayment(Long id, PensionPayment payment) {
        PensionPaymentEntity existingEntity = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        updateEntity(existingEntity, payment);
        return convertToDto(paymentRepository.save(existingEntity));
    }

    public void deletePayment(Long id) {
        System.out.println("Attempting to delete payment with id: " + id);
        PensionPaymentEntity payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        
        // Remove the payment from the pensioner's payments list
        PensionerEntity pensioner = payment.getPensioner();
        if (pensioner != null && pensioner.getPayments() != null) {
            pensioner.getPayments().remove(payment);
        }
        
        // Delete the payment
        paymentRepository.delete(payment);
        System.out.println("Successfully deleted payment with id: " + id);
    }

    public List<PensionPayment> getPaymentsByPeriod(LocalDate startDate, LocalDate endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Double getTotalPaymentsByPensionerId(Long pensionerId) {
        return paymentRepository.getTotalPaymentsByPensionerId(pensionerId);
    }

    private PensionPayment convertToDto(PensionPaymentEntity entity) {
        PensionPayment dto = new PensionPayment();
        dto.setId(entity.getId());
        dto.setPensionerId(entity.getPensioner().getId());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setAmount(entity.getAmount());
        dto.setPaymentType(entity.getPaymentType());
        dto.setStatus(entity.getStatus());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    private PensionPaymentEntity convertToEntity(PensionPayment dto) {
        PensionPaymentEntity entity = new PensionPaymentEntity();
        updateEntity(entity, dto);
        return entity;
    }

    private void updateEntity(PensionPaymentEntity entity, PensionPayment dto) {
        PensionerEntity pensioner = pensionerRepository.findById(dto.getPensionerId())
                .orElseThrow(() -> new RuntimeException("Pensioner not found"));
        
        entity.setPensioner(pensioner);
        entity.setPaymentDate(dto.getPaymentDate());
        entity.setAmount(dto.getAmount());
        entity.setPaymentType(dto.getPaymentType());
        entity.setStatus(dto.getStatus());
        entity.setDescription(dto.getDescription());
    }
} 