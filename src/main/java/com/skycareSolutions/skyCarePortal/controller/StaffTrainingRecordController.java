package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.StaffTrainingRecordDTO;
import com.skycareSolutions.skyCarePortal.service.StaffTrainingExportService;
import com.skycareSolutions.skyCarePortal.service.StaffTrainingRecordService;
import com.skycareSolutions.skyCarePortal.utils.StaffTrainingCertificateFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/staff-training-records")
public class StaffTrainingRecordController {

    @Autowired
    private StaffTrainingRecordService staffTrainingRecordService;

    @Autowired
    private StaffTrainingExportService staffTrainingExportService;

    @GetMapping
    public ResponseEntity<List<StaffTrainingRecordDTO>> list(
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String expiryStatus,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) Boolean competencyAssessed,
            @RequestParam(required = false) Boolean certificateFiled,
            @RequestParam(required = false) String q
    ) {
        return ResponseEntity.ok(staffTrainingRecordService.list(
                requesterRole, requesterEmployeeId, employeeId, course, provider, expiryStatus,
                from, to, competencyAssessed, certificateFiled, q
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffTrainingRecordDTO> getById(
            @PathVariable String id,
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId
    ) {
        return ResponseEntity.ok(staffTrainingRecordService.getById(id, requesterRole, requesterEmployeeId));
    }

    @PostMapping
    public ResponseEntity<StaffTrainingRecordDTO> create(
            @RequestPart("record") String recordJson,
            @RequestPart(value = "certificate", required = false) MultipartFile certificate,
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId
    ) throws Exception {
        return ResponseEntity.ok(staffTrainingRecordService.create(
                recordJson, certificate, requesterRole, requesterEmployeeId
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffTrainingRecordDTO> update(
            @PathVariable String id,
            @RequestPart("record") String recordJson,
            @RequestPart(value = "certificate", required = false) MultipartFile certificate,
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId
    ) throws Exception {
        return ResponseEntity.ok(staffTrainingRecordService.update(
                id, recordJson, certificate, requesterRole, requesterEmployeeId
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            @RequestParam(required = false) String requesterRole
    ) {
        staffTrainingRecordService.delete(id, requesterRole);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/certificate")
    public ResponseEntity<Resource> downloadCertificate(
            @PathVariable String id,
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId
    ) {
        File file = staffTrainingRecordService.getCertificateFile(id, requesterRole, requesterEmployeeId);
        Resource resource = new FileSystemResource(file);
        String mime = StaffTrainingCertificateFileUtil.guessMimeType(file.getName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.parseMediaType(mime))
                .body(resource);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) String requesterRole,
            @RequestParam(required = false) String requesterEmployeeId,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String expiryStatus,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) Boolean competencyAssessed,
            @RequestParam(required = false) Boolean certificateFiled,
            @RequestParam(required = false) String q
    ) {
        byte[] csv = staffTrainingExportService.exportCsv(
                requesterRole, requesterEmployeeId, employeeId, course, provider, expiryStatus,
                from, to, competencyAssessed, certificateFiled, q
        );
        String filename = "staff-training-register-" + LocalDate.now() + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
