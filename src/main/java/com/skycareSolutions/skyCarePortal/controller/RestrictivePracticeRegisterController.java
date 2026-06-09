package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.RestrictivePracticeRegisterDTO;
import com.skycareSolutions.skyCarePortal.service.BehaviourSupportExportService;
import com.skycareSolutions.skyCarePortal.service.RestrictivePracticeRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/restrictive-practice-registers")
public class RestrictivePracticeRegisterController {

    @Autowired
    private RestrictivePracticeRegisterService service;
    @Autowired
    private BehaviourSupportExportService exportService;

    @GetMapping
    public ResponseEntity<List<RestrictivePracticeRegisterDTO>> list(
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId,
            @RequestParam(required = false) String clientId,
            @RequestParam(required = false) String ndisNumber,
            @RequestParam(required = false) String rpType,
            @RequestParam(required = false) String authStatus,
            @RequestParam(required = false) String q
    ) {
        return ResponseEntity.ok(service.list(requesterRole, requesterEmployeeId, clientId, ndisNumber, rpType, authStatus, q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestrictivePracticeRegisterDTO> getById(
            @PathVariable String id,
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId
    ) {
        return ResponseEntity.ok(service.getById(id, requesterRole, requesterEmployeeId));
    }

    @PostMapping
    public ResponseEntity<RestrictivePracticeRegisterDTO> create(
            @RequestBody RestrictivePracticeRegisterDTO dto,
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId
    ) {
        return ResponseEntity.ok(service.create(dto, requesterRole, requesterEmployeeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestrictivePracticeRegisterDTO> update(
            @PathVariable String id,
            @RequestBody RestrictivePracticeRegisterDTO dto,
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
            @RequestParam(required = false) String ndisNumber,
            @RequestParam(required = false) String rpType,
            @RequestParam(required = false) String authStatus,
            @RequestParam(required = false) String q
    ) {
        byte[] csv = exportService.exportRpRegisterCsv(
                requesterRole, requesterEmployeeId, clientId, ndisNumber, rpType, authStatus, q
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"restrictive-practice-register-" + LocalDate.now() + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
