package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.CareGiverDocumentTypesDTO;
import com.skycareSolutions.skyCarePortal.service.CareGiverDocumentTypesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/care-giver-document-types")
public class CareGiverDocumentTypesController {

    @Autowired
    private CareGiverDocumentTypesService service;

    @GetMapping
    public ResponseEntity<List<CareGiverDocumentTypesDTO>> getAllDocumentTypes() {
        return ResponseEntity.ok(service.getAllDocumentTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CareGiverDocumentTypesDTO> getDocumentTypeById(@PathVariable String id) {
        return service.getDocumentTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CareGiverDocumentTypesDTO> createDocumentType(@RequestBody CareGiverDocumentTypesDTO dto) {
        return ResponseEntity.ok(service.createDocumentType(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CareGiverDocumentTypesDTO> updateDocumentType(@PathVariable String id, @RequestBody CareGiverDocumentTypesDTO dto) {
        CareGiverDocumentTypesDTO updated = service.updateDocumentType(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentType(@PathVariable String id) {
        service.deleteDocumentType(id);
        return ResponseEntity.noContent().build();
    }
}
