package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.EmailTemplateDTO;
import com.skycareSolutions.skyCarePortal.dto.EmailPlaceholderItemDTO;
import com.skycareSolutions.skyCarePortal.dto.EmailTemplatePreviewRequestDTO;
import com.skycareSolutions.skyCarePortal.dto.EmailTemplatePreviewResponseDTO;
import com.skycareSolutions.skyCarePortal.dto.EmailTemplateTestSendDTO;
import com.skycareSolutions.skyCarePortal.service.EmailTemplateMergeService;
import com.skycareSolutions.skyCarePortal.service.EmailTemplateAdminService;
import com.skycareSolutions.skyCarePortal.service.EmailTemplateLayoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/email-templates")
public class EmailTemplateController {

    @Autowired
    private EmailTemplateAdminService emailTemplateAdminService;

    @Autowired
    private EmailTemplateMergeService emailTemplateMergeService;

    @GetMapping("/placeholders")
    public ResponseEntity<List<EmailPlaceholderItemDTO>> getPlaceholders() {
        return ResponseEntity.ok(emailTemplateMergeService.getPlaceholderReference());
    }

    @PostMapping("/preview")
    public ResponseEntity<EmailTemplatePreviewResponseDTO> preview(
            @RequestBody EmailTemplatePreviewRequestDTO dto
    ) {
        return ResponseEntity.ok(emailTemplateAdminService.preview(dto));
    }

    @GetMapping("/logo")
    public ResponseEntity<ClassPathResource> getEmailLogo() {
        ClassPathResource resource = new ClassPathResource(EmailTemplateLayoutService.EMAIL_LOGO_CLASSPATH);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                .body(resource);
    }

    @GetMapping
    public ResponseEntity<List<EmailTemplateDTO>> getAll() {
        return ResponseEntity.ok(emailTemplateAdminService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailTemplateDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(emailTemplateAdminService.getById(id));
    }

    @PostMapping
    public ResponseEntity<EmailTemplateDTO> create(@RequestBody EmailTemplateDTO dto) {
        return ResponseEntity.ok(emailTemplateAdminService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmailTemplateDTO> update(@PathVariable String id, @RequestBody EmailTemplateDTO dto) {
        return ResponseEntity.ok(emailTemplateAdminService.update(id, dto));
    }

    @PostMapping("/test-send")
    public ResponseEntity<Void> testSend(@RequestBody EmailTemplateTestSendDTO dto) {
        emailTemplateAdminService.sendTestEmail(dto);
        return ResponseEntity.ok().build();
    }
}
