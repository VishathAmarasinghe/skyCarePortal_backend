package com.skycareSolutions.skyCarePortal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skycareSolutions.skyCarePortal.dto.IncidentActionTypeAllAnswersDTO;
import com.skycareSolutions.skyCarePortal.dto.IncidentsDTO;
import com.skycareSolutions.skyCarePortal.dto.NotesDTO;
import com.skycareSolutions.skyCarePortal.service.IncidentActionTypeAllAnswersService;
import com.skycareSolutions.skyCarePortal.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/incidents")
public class IncidentController {

    private final ObjectMapper objectMapper;

    public IncidentController(ObjectMapper objectMapper) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Autowired
    private IncidentService incidentService;

    // Get all incidents
    @GetMapping
    public ResponseEntity<List<IncidentsDTO>> getAllIncidents() {
        return ResponseEntity.ok(incidentService.getAllIncidents());
    }

    @GetMapping("/employee/{employeeID}")
    public ResponseEntity<List<IncidentsDTO>> getIncidentsRelatedToEmployee(@PathVariable String employeeID) {
        return ResponseEntity.ok(incidentService.getAllIncidentsByEmployeeID(employeeID));
    }

    // Get an incident by ID
    @GetMapping("/{id}")
    public ResponseEntity<IncidentsDTO> getIncidentById(@PathVariable String id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
    }

    // Create a new incident
    @PostMapping
    public ResponseEntity<IncidentsDTO> createIncident(
            @RequestPart("incident") String incidentsDTO,
            @RequestPart(value = "files",required = false) List<MultipartFile> files) {
        try {
            IncidentsDTO incident = objectMapper.readValue(incidentsDTO, IncidentsDTO.class);
            return ResponseEntity.ok(incidentService.createIncident(incident,files));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Update an existing incident
    @PutMapping("/{id}")
    public ResponseEntity<IncidentsDTO> updateIncident(
            @PathVariable String id,
            @RequestPart("incident") String incidentsDTO,
                                                       @RequestPart(value = "files",required = false) List<MultipartFile> files) {
        try {
            IncidentsDTO incident = objectMapper.readValue(incidentsDTO, IncidentsDTO.class);
            return ResponseEntity.ok(incidentService.updateIncident(id, incident, files));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Delete an incident
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable String id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }
}
