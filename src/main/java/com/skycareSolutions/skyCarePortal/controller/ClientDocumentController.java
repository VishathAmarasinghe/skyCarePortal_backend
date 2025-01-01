package com.skycareSolutions.skyCarePortal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.ClientDocumentDTO;
import com.skycareSolutions.skyCarePortal.service.ClientDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/client-documents")
public class ClientDocumentController {

    @Autowired
    private ClientDocumentService clientDocumentService;

    private final ObjectMapper objectMapper;

    public ClientDocumentController() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> uploadClientDocuments(@RequestParam("files") List<MultipartFile> files,
                                                             @RequestParam("documentDTOs") String documentDTOs) {
        try {
            // Deserialize the list of ClientDocumentDTOs
            ClientDocumentDTO document = objectMapper.readValue(documentDTOs, ClientDocumentDTO.class);
            List<ClientDocumentDTO> documentList = new ArrayList<>();
            documentList.add(document);

            // Save all the documents and get the list of saved documents
            clientDocumentService.saveClientDocuments(documentList, files);

            return ResponseEntity.ok().body(new ApiResponse("File Created",true));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDocumentDTO> getClientDocumentById(@PathVariable String id) {
        return ResponseEntity.ok(clientDocumentService.getClientDocumentById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClientDocumentDTO>> getAllClientDocuments() {
        return ResponseEntity.ok(clientDocumentService.getAllClientDocuments());
    }

    @GetMapping("/clients/{clientID}")
    public ResponseEntity<List<ClientDocumentDTO>> getAllClientDocumentsByClientID(@PathVariable(name = "clientID") String clientID) {
        return ResponseEntity.ok(clientDocumentService.getAllDocumentsRelatedTOClient(clientID));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientDocument(@PathVariable String id) {
        clientDocumentService.deleteClientDocument(id);
        return ResponseEntity.noContent().build();
    }
}
