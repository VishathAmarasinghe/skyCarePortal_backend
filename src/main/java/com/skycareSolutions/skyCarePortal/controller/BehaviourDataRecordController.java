package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.BehaviourDataRecordDTO;
import com.skycareSolutions.skyCarePortal.service.BehaviourDataRecordService;
import com.skycareSolutions.skyCarePortal.service.BehaviourSupportExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/behaviour-data-records")
public class BehaviourDataRecordController {

    @Autowired
    private BehaviourDataRecordService service;
    @Autowired
    private BehaviourSupportExportService exportService;

    @GetMapping
    public ResponseEntity<List<BehaviourDataRecordDTO>> list(
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId,
            @RequestParam(required = false) String clientId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String intensity,
            @RequestParam(required = false) Boolean followUpRequired,
            @RequestParam(required = false) String q
    ) {
        return ResponseEntity.ok(service.list(requesterRole, requesterEmployeeId, clientId, from, to, intensity, followUpRequired, q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BehaviourDataRecordDTO> getById(
            @PathVariable String id,
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId
    ) {
        return ResponseEntity.ok(service.getById(id, requesterRole, requesterEmployeeId));
    }

    @PostMapping
    public ResponseEntity<BehaviourDataRecordDTO> create(
            @RequestBody BehaviourDataRecordDTO dto,
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId
    ) {
        return ResponseEntity.ok(service.create(dto, requesterRole, requesterEmployeeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BehaviourDataRecordDTO> update(
            @PathVariable String id,
            @RequestBody BehaviourDataRecordDTO dto,
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId
    ) {
        return ResponseEntity.ok(service.update(id, dto, requesterRole, requesterEmployeeId));
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
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String intensity,
            @RequestParam(required = false) Boolean followUpRequired,
            @RequestParam(required = false) String q
    ) {
        byte[] csv = exportService.exportBehaviourDataCsv(
                requesterRole, requesterEmployeeId, clientId, from, to, intensity, followUpRequired, q
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"behaviour-data-" + LocalDate.now() + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
