package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.CareGiverPaymentTypesDTO;
import com.skycareSolutions.skyCarePortal.service.CareGiverPaymentTypesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/care-giver-payment-types")
public class CareGiverPaymentTypesController {

    @Autowired
    private CareGiverPaymentTypesService service;

    @GetMapping
    public ResponseEntity<List<CareGiverPaymentTypesDTO>> getAllPaymentTypes() {
        return ResponseEntity.ok(service.getAllPaymentTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CareGiverPaymentTypesDTO> getPaymentTypeById(@PathVariable String id) {
        return service.getPaymentTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CareGiverPaymentTypesDTO> createPaymentType(@RequestBody CareGiverPaymentTypesDTO dto) {
        return ResponseEntity.ok(service.createPaymentType(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CareGiverPaymentTypesDTO> updatePaymentType(@PathVariable String id, @RequestBody CareGiverPaymentTypesDTO dto) {
        CareGiverPaymentTypesDTO updated = service.updatePaymentType(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentType(@PathVariable String id) {
        service.deletePaymentType(id);
        return ResponseEntity.noContent().build();
    }
}
