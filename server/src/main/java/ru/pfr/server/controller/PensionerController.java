package ru.pfr.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pfr.model.Pensioner;
import ru.pfr.server.entity.PensionerEntity;
import ru.pfr.server.repository.PensionerRepository;
import ru.pfr.server.service.PensionerService;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing pensioners.
 */
@RestController
@RequestMapping("/api/pensioners")
public class PensionerController {

    private final PensionerService pensionerService;

    @Autowired
    public PensionerController(PensionerService pensionerService) {
        this.pensionerService = pensionerService;
    }

    @GetMapping
    public ResponseEntity<List<Pensioner>> getAllPensioners() {
        return ResponseEntity.ok(pensionerService.getAllPensioners());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pensioner> getPensionerById(@PathVariable("id") Long id) {
        return pensionerService.getPensionerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pensioner> createPensioner(@RequestBody Pensioner pensioner) {
        return ResponseEntity.ok(pensionerService.createPensioner(pensioner));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pensioner> updatePensioner(@PathVariable("id") Long id, @RequestBody Pensioner pensioner) {
        return ResponseEntity.ok(pensionerService.updatePensioner(id, pensioner));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePensioner(@PathVariable("id") Long id) {
        pensionerService.deletePensioner(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Pensioner>> searchPensioners(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String snils,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        return ResponseEntity.ok(pensionerService.searchPensioners(lastName, snils, startDate, endDate));
    }

    @GetMapping("/statistics")
    public ResponseEntity<Object> getStatistics() {
        return ResponseEntity.ok(pensionerService.getStatistics());
    }
} 