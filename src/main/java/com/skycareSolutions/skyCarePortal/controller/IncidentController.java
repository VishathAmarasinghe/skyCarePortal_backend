package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.IncidentsDTO;
import com.skycareSolutions.skyCarePortal.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incidents")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    // Get all incidents
    @GetMapping
    public ResponseEntity<List<IncidentsDTO>> getAllIncidents() {
        return ResponseEntity.ok(incidentService.getAllIncidents());
    }

    // Get an incident by ID
    @GetMapping("/{id}")
    public ResponseEntity<IncidentsDTO> getIncidentById(@PathVariable String id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
    }

    // Create a new incident
    @PostMapping
    public ResponseEntity<IncidentsDTO> createIncident(@RequestBody IncidentsDTO incidentsDTO) {
        return ResponseEntity.ok(incidentService.createIncident(incidentsDTO));
    }

    // Update an existing incident
    @PutMapping("/{id}")
    public ResponseEntity<IncidentsDTO> updateIncident(@PathVariable String id, @RequestBody IncidentsDTO incidentsDTO) {
        return ResponseEntity.ok(incidentService.updateIncident(id, incidentsDTO));
    }

    // Delete an incident
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable String id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }
}
