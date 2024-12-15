package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.ResourceDocumentDTO;
import com.skycareSolutions.skyCarePortal.service.ResourceDocumentService;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/documents")
public class ResourceDocumentController {

    private final ResourceDocumentService documentService;

    public ResourceDocumentController(ResourceDocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<ResourceDocumentDTO> uploadDocument(@RequestParam("file") MultipartFile file,
                                                              @RequestParam("documentName") String documentName,
                                                              @RequestParam("documentLocation") String documentLocation) {
        ResourceDocumentDTO dto = new ResourceDocumentDTO();
        dto.setDocumentName(documentName);
        dto.setDocumentLocation(documentLocation);
        return ResponseEntity.ok(documentService.saveDocument(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceDocumentDTO> getDocumentById(@PathVariable String id) {
        return documentService.getDocumentById(id);
    }

    @GetMapping
    public ResponseEntity<List<ResourceDocumentDTO>> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        return documentService.deleteDocument(id);
    }
}
