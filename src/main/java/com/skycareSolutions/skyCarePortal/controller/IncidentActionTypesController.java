package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.IncidentActionTypesDTO;
import com.skycareSolutions.skyCarePortal.service.IncidentActionTypesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incident-action-types")
public class IncidentActionTypesController {

    @Autowired
    private IncidentActionTypesService incidentActionTypesService;

    @GetMapping
    public ResponseEntity<List<IncidentActionTypesDTO>> getAllActionTypes() {
        return ResponseEntity.ok(incidentActionTypesService.getAllActionTypes());
    }

    @PostMapping
    public ResponseEntity<IncidentActionTypesDTO> createActionType(@RequestBody IncidentActionTypesDTO dto) {
        return ResponseEntity.ok(incidentActionTypesService.createActionType(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActionType(@PathVariable String id) {
        incidentActionTypesService.deleteActionType(id);
        return ResponseEntity.noContent().build();
    }
}
