package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.ShiftNoteDocumentDTO;
import com.skycareSolutions.skyCarePortal.model.ShiftNoteDocuments;
import com.skycareSolutions.skyCarePortal.model.ShiftNotes;
import com.skycareSolutions.skyCarePortal.repository.ShiftDocumentRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShiftDocumentService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "shiftNotes").toString();

    @Autowired
    private final ShiftDocumentRepository documentRepository;

    @Autowired
    @Lazy
    private ShiftNoteService shiftNoteService;

    public ShiftDocumentService(ShiftDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    private ShiftNoteDocuments toEntity(ShiftNoteDocumentDTO dto) {
        ShiftNoteDocuments document = new ShiftNoteDocuments();
        document.setDocumentId(dto.getDocumentId());
        document.setDocumentName(dto.getDocumentName());
        document.setDocumentLocation(dto.getDocumentLocation());
        document.setShiftNotes(shiftNoteService.getShiftNotesByIdWithEntity(dto.getShiftNoteID()));
        return document;
    }

    private ShiftNoteDocumentDTO toDTO(ShiftNoteDocuments document) {
        ShiftNoteDocumentDTO dto = new ShiftNoteDocumentDTO();
        dto.setDocumentId(document.getDocumentId());
        dto.setDocumentName(document.getDocumentName());
        dto.setDocumentLocation(document.getDocumentLocation());
        dto.setShiftNoteID(document.getShiftNotes().getNoteID());
        return dto;
    }

    public ShiftNoteDocumentDTO saveDocument(ShiftNoteDocumentDTO dto) {
        String lastDocId = documentRepository.getLastIdOfShiftNotesDocuments();
        String newDocId = Micro_utils.idGenerator("shiftNotesDocuments", lastDocId);
        dto.setDocumentId(newDocId);
        ShiftNoteDocuments document = toEntity(dto);
        ShiftNoteDocuments savedDocument = documentRepository.save(document);
        return toDTO(savedDocument);
    }

    public ResponseEntity<ShiftNoteDocumentDTO> getDocumentById(String documentId) {
        ShiftNoteDocuments document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return ResponseEntity.ok(toDTO(document));
    }

    public ResponseEntity<List<ShiftNoteDocumentDTO>> getAllDocuments() {
        List<ShiftNoteDocumentDTO> documents = documentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(documents);
    }

    public List<ShiftNoteDocumentDTO> getDocumentsRelatedToShiftNoteID(String shiftNoteID) {
        ShiftNotes shiftNote = shiftNoteService.getShiftNotesByIdWithEntity(shiftNoteID);
        List<ShiftNoteDocuments> documents = documentRepository.getShiftNoteDocumentsRelatedToShiftNote(shiftNote);
        List<ShiftNoteDocumentDTO> shiftNoteDocumentDTOS = new ArrayList<>();
        for (ShiftNoteDocuments shiftNoteDocuments:documents){
            shiftNoteDocumentDTOS.add(toDTO(shiftNoteDocuments));
        }
        return shiftNoteDocumentDTOS;
    }

    public ResponseEntity<Void> deleteDocument(String documentId) {
        Optional<ShiftNoteDocuments> document = documentRepository.findById(documentId);
        documentRepository.deleteById(documentId);

        document.ifPresent(doc -> {
            try {
                String fileName = doc.getDocumentLocation().substring(doc.getDocumentLocation().lastIndexOf("\\"));
                File destinationFile = new File(DIRECTORY, fileName);
                if (destinationFile.exists() && !destinationFile.delete()) {
                    throw new RuntimeException("Failed to delete the file: " + destinationFile.getName());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error while deleting the document file", e);
            }
        });

        return ResponseEntity.noContent().build();
    }


    public ResponseEntity<Void> deleteAllDocumentsRelatedToShiftNote(String shiftNoteID) {
        ShiftNotes shiftNote = shiftNoteService.getShiftNotesByIdWithEntity(shiftNoteID);

        List<ShiftNoteDocuments> documents = documentRepository.getShiftNoteDocumentsRelatedToShiftNote(shiftNote);

        documents.forEach(document -> {
            documentRepository.deleteById(document.getDocumentId());
            try {
                String fileName = document.getDocumentLocation().substring(document.getDocumentLocation().lastIndexOf("\\"));
                File destinationFile = new File(DIRECTORY, fileName);
                if (destinationFile.exists() && !destinationFile.delete()) {
                    throw new RuntimeException("Failed to delete the file: " + destinationFile.getName());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error while deleting the document file", e);
            }
        });

        return ResponseEntity.noContent().build();
    }
}
