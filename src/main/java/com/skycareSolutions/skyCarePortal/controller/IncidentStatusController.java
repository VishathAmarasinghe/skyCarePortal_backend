package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.IncidentStatusDTO;
import com.skycareSolutions.skyCarePortal.service.IncidentStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incident-status")
public class IncidentStatusController {

    @Autowired
    private IncidentStatusService incidentStatusService;

    @GetMapping
    public ResponseEntity<List<IncidentStatusDTO>> getAllStatuses() {
        return ResponseEntity.ok(incidentStatusService.getAllStatuses());
    }

    @PostMapping
    public ResponseEntity<IncidentStatusDTO> createStatus(@RequestBody IncidentStatusDTO dto) {
        return ResponseEntity.ok(incidentStatusService.createStatus(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidentStatusDTO> updateStatus(@PathVariable String id, @RequestBody IncidentStatusDTO dto) {
        return ResponseEntity.ok(incidentStatusService.updateStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable String id) {
        incidentStatusService.deleteStatus(id);
        return ResponseEntity.noContent().build();
    }
}
