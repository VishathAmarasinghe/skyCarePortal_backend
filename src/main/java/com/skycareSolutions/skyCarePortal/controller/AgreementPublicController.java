package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.service.AgreementPublicService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/public/agreements")
public class AgreementPublicController {

    @Autowired
    private AgreementPublicService publicService;

    @GetMapping("/{token}")
    public ResponseEntity<AgreementPublicDocumentDTO> getDocument(
            @PathVariable String token,
            @RequestParam(required = false) String email,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(publicService.getDocument(token, clientIp(request), email));
    }

    @PostMapping("/{token}/sign")
    public ResponseEntity<AgreementPublicDocumentDTO> sign(
            @PathVariable String token,
            @RequestBody AgreementRecipientSignDTO dto,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(publicService.sign(token, dto, clientIp(request)));
    }

    @GetMapping("/{token}/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable String token,
            HttpServletRequest request
    ) {
        byte[] pdf = publicService.downloadPdf(token, clientIp(request));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"agreement.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/{token}/pdf/preview")
    public ResponseEntity<byte[]> downloadPreviewPdf(
            @PathVariable String token,
            @RequestBody(required = false) AgreementPublicPreviewDTO dto,
            HttpServletRequest request
    ) {
        byte[] pdf = publicService.downloadPreviewPdf(token, dto, clientIp(request));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"agreement-preview.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/{token}/preview-html")
    public ResponseEntity<AgreementPublicDocumentDTO> previewHtml(
            @PathVariable String token,
            @RequestBody AgreementPublicPreviewDTO dto,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(publicService.previewHtml(token, dto, clientIp(request)));
    }

    @GetMapping("/{token}/reference-materials/{attachmentId}")
    public ResponseEntity<byte[]> downloadReferenceMaterial(
            @PathVariable String token,
            @PathVariable String attachmentId,
            HttpServletRequest request
    ) {
        AgreementReferenceMaterialSummaryDTO meta =
                publicService.getReferenceMaterialMeta(token, attachmentId, clientIp(request));
        byte[] bytes = publicService.downloadReferenceMaterial(token, attachmentId, clientIp(request));
        String filename = meta.getOriginalFilename() != null ? meta.getOriginalFilename() : "reference-material";
        MediaType mediaType = meta.getMimeType() != null
                ? MediaType.parseMediaType(meta.getMimeType())
                : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .body(bytes);
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
