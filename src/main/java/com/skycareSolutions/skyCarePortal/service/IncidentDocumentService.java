package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.IncidentDocumentsDTO;
import com.skycareSolutions.skyCarePortal.model.IncidentDocuments;
import com.skycareSolutions.skyCarePortal.model.Incidents;
import com.skycareSolutions.skyCarePortal.repository.IncidentDocumentsRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IncidentDocumentService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "incidents").toString();

    @Autowired
    private IncidentDocumentsRepository documentRepository;

    @Autowired
    private IncidentService incidentService;

    private IncidentDocuments toEntity(IncidentDocumentsDTO dto) {
        IncidentDocuments document = new IncidentDocuments();
        document.setDocumentID(dto.getDocumentID());
        document.setDocumentName(dto.getDocumentName());
        document.setDocumentLocation(dto.getDocumentLocation());
        document.setIncidents(incidentService.getIncidentByIDWithEntity(dto.getIncidentID()));
        return document;
    }

    private IncidentDocumentsDTO toDTO(IncidentDocuments document) {
        IncidentDocumentsDTO dto = new IncidentDocumentsDTO();
        dto.setDocumentID(document.getDocumentID());
        dto.setDocumentName(document.getDocumentName());
        dto.setDocumentLocation(document.getDocumentLocation());
        dto.setIncidentID(document.getIncidents().getIncidentID());
        return dto;
    }

    public IncidentDocumentsDTO saveDocument(IncidentDocumentsDTO dto) {
        // Generate ID
        String lastId = documentRepository.getLastIdOfIncidentDocument();
        String newId = Micro_utils.idGenerator("incidentDocuments", lastId);
        dto.setDocumentID(newId);

        IncidentDocuments document = toEntity(dto);
        IncidentDocuments savedDocument = documentRepository.save(document);

        return toDTO(savedDocument);
    }

    public IncidentDocumentsDTO getDocumentById(String documentId) {
        IncidentDocuments document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return toDTO(document);
    }

    public ResponseEntity<List<IncidentDocumentsDTO>> getAllDocuments() {
        List<IncidentDocumentsDTO> documents = documentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(documents);
    }

    public List<IncidentDocumentsDTO> getDocumentsRelatedToIncident(String incidentID) {
        Incidents incident = incidentService.getIncidentByIDWithEntity(incidentID);
        List<IncidentDocuments> documents = documentRepository.getAllDocumentsRelatedToIncident(incident);
        return documents.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ResponseEntity<Void> deleteDocument(String documentId) {
        Optional<IncidentDocuments> document = documentRepository.findById(documentId);
        documentRepository.deleteById(documentId);

        if (document.isPresent()) {
            String fileName = document.get().getDocumentLocation().substring(document.get().getDocumentLocation().lastIndexOf("\\") + 1);
            File file = new File(DIRECTORY, fileName);
            if (file.exists() && !file.delete()) {
                throw new RuntimeException("Failed to delete the file: " + fileName);
            }
        }

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Void> deleteAllDocumentsRelatedToIncident(String incidentID) {
        Incidents incident = incidentService.getIncidentByIDWithEntity(incidentID);
        List<IncidentDocuments> documents = documentRepository.getAllDocumentsRelatedToIncident(incident);

        for (IncidentDocuments document : documents) {
            documentRepository.deleteById(document.getDocumentID());
            String fileName = document.getDocumentLocation().substring(document.getDocumentLocation().lastIndexOf("\\") + 1);
            File file = new File(DIRECTORY, fileName);
            if (file.exists() && !file.delete()) {
                throw new RuntimeException("Failed to delete the file: " + fileName);
            }
        }

        return ResponseEntity.noContent().build();
    }
}
