package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.service.AgreementTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/agreement-template-versions")
public class AgreementTemplateVersionController {

    @Autowired
    private AgreementTemplateService templateService;

    @GetMapping("/{id}")
    public ResponseEntity<AgreementTemplateVersionDTO> getVersion(@PathVariable String id) {
        return ResponseEntity.ok(templateService.getVersion(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgreementTemplateVersionDTO> updateVersion(
            @PathVariable String id,
            @RequestBody AgreementVersionUpdateDTO dto
    ) {
        return ResponseEntity.ok(templateService.updateDraftVersion(id, dto));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<AgreementTemplateVersionDTO> publish(
            @PathVariable String id,
            @RequestParam(value = "publishedBy", required = false) String publishedBy
    ) {
        return ResponseEntity.ok(templateService.publishVersion(id, publishedBy));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<AgreementTemplateVersionDTO> archive(@PathVariable String id) {
        return ResponseEntity.ok(templateService.archiveVersion(id));
    }

    @PostMapping("/{id}/preview")
    public ResponseEntity<AgreementPreviewResponseDTO> preview(
            @PathVariable String id,
            @RequestBody(required = false) AgreementPreviewRequestDTO request
    ) {
        if (request == null) {
            request = new AgreementPreviewRequestDTO();
        }
        return ResponseEntity.ok(templateService.previewVersion(id, request));
    }
}
