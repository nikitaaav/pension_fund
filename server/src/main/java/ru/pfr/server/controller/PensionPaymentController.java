package ru.pfr.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pfr.model.PensionPayment;
import ru.pfr.server.service.PensionPaymentService;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing pension payments.
 */
@RestController
@RequestMapping("/api/payments")
public class PensionPaymentController {

    private final PensionPaymentService paymentService;

    @Autowired
    public PensionPaymentController(PensionPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<PensionPayment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/pensioner/{pensionerId}")
    public ResponseEntity<List<PensionPayment>> getPaymentsByPensionerId(@PathVariable("pensionerId") Long pensionerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByPensionerId(pensionerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PensionPayment> getPaymentById(@PathVariable("id") Long id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PensionPayment> createPayment(@RequestBody PensionPayment payment) {
        return ResponseEntity.ok(paymentService.createPayment(payment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PensionPayment> updatePayment(@PathVariable("id") Long id, @RequestBody PensionPayment payment) {
        return ResponseEntity.ok(paymentService.updatePayment(id, payment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable("id") Long id) {
        try {
            System.out.println("Controller: Attempting to delete payment with id: " + id);
            paymentService.deletePayment(id);
            System.out.println("Controller: Successfully deleted payment with id: " + id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Controller: Error deleting payment with id: " + id + ", error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/period")
    public ResponseEntity<List<PensionPayment>> getPaymentsByPeriod(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(paymentService.getPaymentsByPeriod(startDate, endDate));
    }

    @GetMapping("/pensioner/{pensionerId}/total")
    public ResponseEntity<Double> getTotalPaymentsByPensionerId(@PathVariable Long pensionerId) {
        return ResponseEntity.ok(paymentService.getTotalPaymentsByPensionerId(pensionerId));
    }
} 