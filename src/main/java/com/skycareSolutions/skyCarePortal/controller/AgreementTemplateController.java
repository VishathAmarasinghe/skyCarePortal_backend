package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.AgreementTemplateAsset;
import com.skycareSolutions.skyCarePortal.service.AgreementPlaceholderCatalogService;
import com.skycareSolutions.skyCarePortal.service.AgreementTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/agreement-templates")
public class AgreementTemplateController {

    @Autowired
    private AgreementTemplateService templateService;

    @Autowired
    private AgreementPlaceholderCatalogService placeholderCatalogService;

    @GetMapping
    public ResponseEntity<List<AgreementTemplateDTO>> getAll() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgreementTemplateDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(templateService.getTemplateById(id));
    }

    @PostMapping
    public ResponseEntity<AgreementTemplateDTO> create(@RequestBody AgreementTemplateCreateDTO dto) {
        return ResponseEntity.ok(templateService.createTemplate(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgreementTemplateDTO> update(
            @PathVariable String id,
            @RequestBody AgreementTemplateUpdateDTO dto
    ) {
        return ResponseEntity.ok(templateService.updateTemplate(id, dto));
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<List<AgreementTemplateVersionSummaryDTO>> getVersions(@PathVariable String id) {
        return ResponseEntity.ok(templateService.getVersions(id));
    }

    @PostMapping("/{id}/versions")
    public ResponseEntity<AgreementTemplateVersionDTO> createVersion(
            @PathVariable String id,
            @RequestBody(required = false) AgreementVersionUpdateDTO dto
    ) {
        String notes = dto != null && dto.getChangeNotes() != null ? dto.getChangeNotes() : "New draft version";
        return ResponseEntity.ok(templateService.createDraftVersion(id, notes));
    }

    @PostMapping("/assets/upload")
    public ResponseEntity<AgreementAssetUploadResponseDTO> uploadAsset(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "templateId", required = false) String templateId
    ) throws Exception {
        return ResponseEntity.ok(templateService.uploadAsset(file, templateId));
    }

    @GetMapping("/assets/{assetId}")
    public ResponseEntity<FileSystemResource> getAsset(@PathVariable String assetId) {
        AgreementTemplateAsset asset = templateService.getAsset(assetId);
        File file = new File(asset.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        FileSystemResource resource = new FileSystemResource(file);
        MediaType mediaType = asset.getMimeType() != null
                ? MediaType.parseMediaType(asset.getMimeType())
                : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .contentType(mediaType)
                .body(resource);
    }
}
