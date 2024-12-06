package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.CareGiverDocumentsDTO;
import com.skycareSolutions.skyCarePortal.service.CareGiverDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/care-giver-documents")
public class CareGiverDocumentsController {

    @Autowired
    private CareGiverDocumentService careGiverDocumentService;

    @GetMapping
    public ResponseEntity<List<CareGiverDocumentsDTO>> getAllDocuments() {
        return ResponseEntity.ok(careGiverDocumentService.getAllDocuments());
    }

    @GetMapping("/{careGiverID}")
    public ResponseEntity<List<CareGiverDocumentsDTO>> getDocumentsByCareGiverId(@PathVariable String careGiverID) {
        List<CareGiverDocumentsDTO> documents = careGiverDocumentService.getDocumentsByCareGiverId(careGiverID);
        return documents.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(documents);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addDocument(@RequestBody List<CareGiverDocumentsDTO> dtos, List<MultipartFile> files) {
        careGiverDocumentService.addDocument(dtos,files);
        return ResponseEntity.ok(new ApiResponse("Documents added successfully",true));
    }

    @PutMapping("/{careGiverID}/{documentTypeID}")
    public ResponseEntity<CareGiverDocumentsDTO> updateDocument(
            @PathVariable String careGiverID,
            @PathVariable String documentTypeID,
            @RequestBody CareGiverDocumentsDTO dto
    ) {
        return ResponseEntity.ok(careGiverDocumentService.updateDocument(careGiverID, documentTypeID, dto));
    }

    @DeleteMapping("/{careGiverID}/{documentTypeID}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String careGiverID, @PathVariable String documentTypeID) {
        careGiverDocumentService.deleteDocument(careGiverID, documentTypeID);
        return ResponseEntity.noContent().build();
    }
}
