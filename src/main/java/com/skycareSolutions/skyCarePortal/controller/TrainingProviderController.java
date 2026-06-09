package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.ComplianceLookupDTO;
import com.skycareSolutions.skyCarePortal.model.ComplianceLookupType;
import com.skycareSolutions.skyCarePortal.service.ComplianceLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/training-providers")
public class TrainingProviderController {

    @Autowired
    private ComplianceLookupService complianceLookupService;

    @GetMapping
    public ResponseEntity<List<ComplianceLookupDTO>> getAll() {
        return ResponseEntity.ok(complianceLookupService.getByType(ComplianceLookupType.TRAINING_PROVIDER));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplianceLookupDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(complianceLookupService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ComplianceLookupDTO> create(@RequestBody ComplianceLookupDTO dto) {
        return ResponseEntity.ok(complianceLookupService.create(ComplianceLookupType.TRAINING_PROVIDER, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComplianceLookupDTO> update(@PathVariable String id, @RequestBody ComplianceLookupDTO dto) {
        return ResponseEntity.ok(complianceLookupService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        complianceLookupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
