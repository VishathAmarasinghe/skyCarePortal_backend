package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.OrganizationSettingDTO;
import com.skycareSolutions.skyCarePortal.model.OrganizationSetting;
import com.skycareSolutions.skyCarePortal.service.OrganizationSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@CrossOrigin("*")
@RequestMapping("/settings/organization")
public class OrganizationSettingController {

    @Autowired
    private OrganizationSettingService organizationSettingService;

    @GetMapping
    public ResponseEntity<OrganizationSettingDTO> get() {
        return ResponseEntity.ok(organizationSettingService.get());
    }

    @PutMapping
    public ResponseEntity<OrganizationSettingDTO> update(@RequestBody OrganizationSettingDTO dto) {
        return ResponseEntity.ok(organizationSettingService.update(dto));
    }

    @PostMapping("/logo")
    public ResponseEntity<OrganizationSettingDTO> uploadLogo(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(organizationSettingService.uploadLogo(file));
    }

    @GetMapping("/logo")
    public ResponseEntity<FileSystemResource> getLogo() {
        OrganizationSetting entity = organizationSettingService.getEntity();
        if (entity.getLogoPath() == null || entity.getLogoPath().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(entity.getLogoPath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(new FileSystemResource(file));
    }
}
