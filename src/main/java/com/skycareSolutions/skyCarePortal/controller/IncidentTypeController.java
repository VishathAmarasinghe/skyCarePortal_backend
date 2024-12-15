package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.IncidentTypeDTO;
import com.skycareSolutions.skyCarePortal.service.IncidentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/incident-types")
public class IncidentTypeController {

    @Autowired
    private IncidentTypeService incidentTypeService;

    @GetMapping
    public ResponseEntity<List<IncidentTypeDTO>> getAllIncidentTypes() {
        return ResponseEntity.ok(incidentTypeService.getAllIncidentTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentTypeDTO> getIncidentTypeById(@PathVariable String id) {
        return ResponseEntity.ok(incidentTypeService.getIncidentTypeById(id));
    }

    @PostMapping
    public ResponseEntity<IncidentTypeDTO> createIncidentType(@RequestBody IncidentTypeDTO dto) {
        return ResponseEntity.ok(incidentTypeService.createIncidentType(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidentTypeDTO> updateIncidentType(@PathVariable String id, @RequestBody IncidentTypeDTO dto) {
        return ResponseEntity.ok(incidentTypeService.updateIncidentType(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncidentType(@PathVariable String id) {
        incidentTypeService.deleteIncidentType(id);
        return ResponseEntity.noContent().build();
    }
}
