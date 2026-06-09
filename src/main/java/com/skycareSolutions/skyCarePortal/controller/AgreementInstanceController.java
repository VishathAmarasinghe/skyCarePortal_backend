package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.service.AgreementInstanceService;
import com.skycareSolutions.skyCarePortal.service.AgreementBulkSendService;
import com.skycareSolutions.skyCarePortal.service.AgreementExportService;
import com.skycareSolutions.skyCarePortal.service.AgreementValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.skycareSolutions.skyCarePortal.utils.AgreementLogoUtil;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/agreements")
public class AgreementInstanceController {

    @Autowired
    private AgreementInstanceService instanceService;

    @Autowired
    private AgreementValidationService validationService;

    @Autowired
    private AgreementBulkSendService bulkSendService;

    @Autowired
    private AgreementExportService exportService;

    @GetMapping("/applicable")
    public ResponseEntity<List<AgreementApplicableTemplateDTO>> getApplicable(
            @RequestParam String recipientType,
            @RequestParam String recipientId
    ) {
        return ResponseEntity.ok(instanceService.getApplicableTemplates(recipientType, recipientId));
    }

    @GetMapping("/default-logo")
    public ResponseEntity<ClassPathResource> getDefaultLogo() {
        ClassPathResource resource = new ClassPathResource("agreement-assets/skycare_logo_ndis.png");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                .body(resource);
    }

    @GetMapping("/by-recipient")
    public ResponseEntity<List<AgreementInstanceDTO>> getByRecipient(
            @RequestParam String recipientType,
            @RequestParam String recipientId
    ) {
        return ResponseEntity.ok(instanceService.getByRecipient(recipientType, recipientId));
    }

    @PostMapping("/instances")
    public ResponseEntity<AgreementInstanceDTO> createInstance(@RequestBody AgreementInstanceCreateDTO dto) {
        return ResponseEntity.ok(instanceService.createInstance(dto));
    }

    @PutMapping("/instances/{id}/content")
    public ResponseEntity<AgreementInstanceDTO> updateContent(
            @PathVariable String id,
            @RequestBody AgreementInstanceContentDTO dto,
            @RequestParam(required = false) String actorId
    ) {
        return ResponseEntity.ok(instanceService.updateContent(id, dto, actorId != null ? actorId : "admin"));
    }

    @PostMapping("/instances/{id}/admin-sign")
    public ResponseEntity<AgreementInstanceDTO> adminSign(
            @PathVariable String id,
            @RequestBody AgreementAdminSignDTO dto
    ) {
        return ResponseEntity.ok(instanceService.adminSign(id, dto));
    }

    @GetMapping("/instances/{id}/preview")
    public ResponseEntity<AgreementPreviewResponseDTO> preview(@PathVariable String id) {
        return ResponseEntity.ok(instanceService.previewInstance(id));
    }

    @GetMapping("/instances/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String id) {
        byte[] pdf = instanceService.downloadPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"agreement-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/instances/{id}/events")
    public ResponseEntity<List<AgreementEventDTO>> getEvents(@PathVariable String id) {
        return ResponseEntity.ok(instanceService.getEvents(id));
    }

    @GetMapping("/instances/{id}/reference-materials")
    public ResponseEntity<List<AgreementReferenceMaterialSummaryDTO>> getReferenceMaterials(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(instanceService.getInstanceReferenceMaterials(id));
    }

    @PostMapping("/instances/{id}/send")
    public ResponseEntity<AgreementSendResponseDTO> send(
            @PathVariable String id,
            @RequestBody AgreementSendDTO dto
    ) {
        return ResponseEntity.ok(instanceService.sendInstance(id, dto));
    }

    @PostMapping("/instances/{id}/send-manual")
    public ResponseEntity<AgreementSendResponseDTO> sendManual(
            @PathVariable String id,
            @RequestBody AgreementManualSendDTO dto
    ) {
        return ResponseEntity.ok(instanceService.markSentManually(id, dto));
    }

    @PostMapping("/instances/{id}/signing-link")
    public ResponseEntity<AgreementSigningLinkDTO> signingLink(
            @PathVariable String id,
            @RequestParam(required = false) String actorId,
            @RequestParam(defaultValue = "true") boolean regenerate
    ) {
        return ResponseEntity.ok(instanceService.generateSigningLink(
                id, actorId != null ? actorId : "admin", regenerate));
    }

    @PostMapping(value = "/instances/{id}/upload-signed-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AgreementInstanceDTO> uploadSignedPdf(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String actorId,
            @RequestParam(required = false) String notes
    ) throws Exception {
        return ResponseEntity.ok(instanceService.uploadSignedPdf(
                id, file.getBytes(), actorId != null ? actorId : "admin", notes));
    }

    @PostMapping("/instances/{id}/clear-admin-signature")
    public ResponseEntity<AgreementInstanceDTO> clearAdminSignature(
            @PathVariable String id,
            @RequestParam(required = false) String actorId
    ) {
        return ResponseEntity.ok(instanceService.clearAdminSignature(id, actorId != null ? actorId : "admin"));
    }

    @PutMapping("/instances/{id}/status")
    public ResponseEntity<AgreementInstanceDTO> updateStatus(
            @PathVariable String id,
            @RequestBody AgreementStatusUpdateDTO dto
    ) {
        return ResponseEntity.ok(instanceService.updateStatus(id, dto));
    }

    @PostMapping("/instances/{id}/resend")
    public ResponseEntity<AgreementSendResponseDTO> resend(
            @PathVariable String id,
            @RequestParam(required = false) String actorId,
            @RequestParam(defaultValue = "true") boolean sendEmail
    ) {
        return ResponseEntity.ok(instanceService.resendInstance(
                id, actorId != null ? actorId : "admin", sendEmail));
    }

    @PostMapping("/instances/{id}/cancel")
    public ResponseEntity<AgreementInstanceDTO> cancel(
            @PathVariable String id,
            @RequestParam(required = false) String actorId
    ) {
        return ResponseEntity.ok(instanceService.cancelInstance(id, actorId != null ? actorId : "admin"));
    }

    @DeleteMapping("/instances/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            @RequestParam(required = false) String actorId
    ) {
        instanceService.deleteInstance(id, actorId != null ? actorId : "admin");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/instances/{id}/validate")
    public ResponseEntity<AgreementValidationResultDTO> validate(@PathVariable String id) {
        return ResponseEntity.ok(validationService.validateInstance(id));
    }

    @PostMapping("/bulk-send")
    public ResponseEntity<AgreementBulkSendResultDTO> bulkSend(@RequestBody AgreementBulkSendDTO dto) {
        return ResponseEntity.ok(bulkSendService.bulkSend(dto));
    }

    @GetMapping("/instances/{id}/events/export")
    public ResponseEntity<byte[]> exportEvents(@PathVariable String id) {
        byte[] csv = exportService.exportInstanceEventsCsv(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"agreement-events-" + id + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping("/history/export")
    public ResponseEntity<byte[]> exportHistory(
            @RequestParam String recipientType,
            @RequestParam String recipientId
    ) {
        byte[] csv = exportService.exportRecipientHistoryCsv(recipientType, recipientId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"agreement-history.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
