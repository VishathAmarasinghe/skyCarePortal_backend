package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.RestrictivePracticeEvidenceDTO;
import com.skycareSolutions.skyCarePortal.service.BehaviourSupportExportService;
import com.skycareSolutions.skyCarePortal.service.RestrictivePracticeEvidenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/restrictive-practice-evidence")
public class RestrictivePracticeEvidenceController {

    @Autowired
    private RestrictivePracticeEvidenceService service;
    @Autowired
    private BehaviourSupportExportService exportService;

    @GetMapping
    public ResponseEntity<List<RestrictivePracticeEvidenceDTO>> list(
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId,
            @RequestParam(required = false) String clientId,
            @RequestParam(required = false) String rpType,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String q
    ) {
        return ResponseEntity.ok(service.list(requesterRole, requesterEmployeeId, clientId, rpType, from, to, q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestrictivePracticeEvidenceDTO> getById(
            @PathVariable String id,
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId
    ) {
        return ResponseEntity.ok(service.getById(id, requesterRole, requesterEmployeeId));
    }

    @PostMapping
    public ResponseEntity<RestrictivePracticeEvidenceDTO> create(
            @RequestBody RestrictivePracticeEvidenceDTO dto,
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId
    ) {
        return ResponseEntity.ok(service.create(dto, requesterRole, requesterEmployeeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestrictivePracticeEvidenceDTO> update(
            @PathVariable String id,
            @RequestBody RestrictivePracticeEvidenceDTO dto,
            @RequestParam(required = false) String requesterRole
    ) {
        return ResponseEntity.ok(service.update(id, dto, requesterRole));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, @RequestParam(required = false) String requesterRole) {
        service.delete(id, requesterRole);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId,
            @RequestParam(required = false) String clientId,
            @RequestParam(required = false) String rpType,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String q
    ) {
        byte[] csv = exportService.exportEvidenceCsv(
                requesterRole, requesterEmployeeId, clientId, rpType, from, to, q
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rp-evidence-" + LocalDate.now() + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
