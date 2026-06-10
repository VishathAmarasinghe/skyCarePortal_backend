package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.AgreementReferenceMaterialDTO;
import com.skycareSolutions.skyCarePortal.dto.AgreementReferenceMaterialUpdateDTO;
import com.skycareSolutions.skyCarePortal.model.AgreementReferenceMaterial;
import com.skycareSolutions.skyCarePortal.service.AgreementReferenceMaterialService;
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
@RequestMapping("/agreement-reference-materials")
public class AgreementReferenceMaterialController {

    @Autowired
    private AgreementReferenceMaterialService referenceMaterialService;

    @GetMapping
    public ResponseEntity<List<AgreementReferenceMaterialDTO>> list(
            @RequestParam(required = false) String recipientType,
            @RequestParam(required = false) String careGiverType
    ) {
        if (recipientType != null && !recipientType.isBlank()) {
            return ResponseEntity.ok(referenceMaterialService.listForRecipient(recipientType, careGiverType));
        }
        return ResponseEntity.ok(referenceMaterialService.listAll());
    }

    @PostMapping("/upload")
    public ResponseEntity<AgreementReferenceMaterialDTO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "audience", required = false, defaultValue = "BOTH") String audience,
            @RequestParam(value = "careGiverType", required = false) String careGiverType,
            @RequestParam(value = "sortOrder", required = false) Integer sortOrder,
            @RequestParam(value = "createdBy", required = false) String createdBy
    ) throws Exception {
        return ResponseEntity.ok(referenceMaterialService.upload(
                file, title, description, audience, careGiverType, sortOrder, createdBy));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgreementReferenceMaterialDTO> update(
            @PathVariable String id,
            @RequestBody AgreementReferenceMaterialUpdateDTO dto
    ) {
        return ResponseEntity.ok(referenceMaterialService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable String id) {
        referenceMaterialService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<FileSystemResource> download(@PathVariable String id) {
        AgreementReferenceMaterial material = referenceMaterialService.getEntity(id);
        File file = referenceMaterialService.resolveMaterialFile(id);
        FileSystemResource resource = new FileSystemResource(file);
        MediaType mediaType = material.getMimeType() != null
                ? MediaType.parseMediaType(material.getMimeType())
                : MediaType.APPLICATION_OCTET_STREAM;
        String filename = material.getOriginalFilename() != null
                ? material.getOriginalFilename()
                : file.getName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .body(resource);
    }
}
