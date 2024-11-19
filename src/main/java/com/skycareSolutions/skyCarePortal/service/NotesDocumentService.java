package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.NoteDocumentsDTO;
import com.skycareSolutions.skyCarePortal.model.NoteDocuments;
import com.skycareSolutions.skyCarePortal.model.NoteDocumentsKey;
import com.skycareSolutions.skyCarePortal.repository.NotesDocumentRepository;
import com.skycareSolutions.skyCarePortal.repository.NotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotesDocumentService {

    @Autowired
    private NotesDocumentRepository notesDocumentRepository;

    @Autowired
    private NotesRepository notesRepository;

    // Create or Update a NoteDocument
    public NoteDocumentsDTO saveNoteDocument(NoteDocumentsDTO noteDocumentsDTO) {
        NoteDocuments noteDocument = new NoteDocuments();
        NoteDocumentsKey key = new NoteDocumentsKey(noteDocumentsDTO.getNoteID(), noteDocumentsDTO.getDocID());
        noteDocument.setId(key);
        noteDocument.setDocument(noteDocumentsDTO.getDocument());

        // Validate and map the related Notes entity
        notesRepository.findById(noteDocumentsDTO.getNoteID())
                .ifPresent(noteDocument::setNote);

        notesDocumentRepository.save(noteDocument);
        return noteDocumentsDTO;
    }

    // Retrieve all documents
    public List<NoteDocumentsDTO> getAllNoteDocuments() {
        return ((List<NoteDocuments>) notesDocumentRepository.findAll())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Retrieve documents by noteID
    public List<NoteDocumentsDTO> getDocumentsByNoteID(String noteID) {
        return ((List<NoteDocuments>) notesDocumentRepository.findAll())
                .stream()
                .filter(doc -> doc.getId().getNoteID().equals(noteID))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Delete a document by composite key
    public void deleteNoteDocument(String noteID, String docID) {
        NoteDocumentsKey key = new NoteDocumentsKey(noteID, docID);
        notesDocumentRepository.deleteById(key);
    }

    // Delete all documents by noteID
    public void deleteDocumentsByNoteID(String noteID) {
        List<NoteDocuments> documentsToDelete = ((List<NoteDocuments>) notesDocumentRepository.findAll())
                .stream()
                .filter(doc -> doc.getId().getNoteID().equals(noteID))
                .collect(Collectors.toList());
        notesDocumentRepository.deleteAll(documentsToDelete);
    }

    // Utility to convert Entity to DTO
    private NoteDocumentsDTO convertToDTO(NoteDocuments noteDocument) {
        NoteDocumentsDTO dto = new NoteDocumentsDTO();
        dto.setNoteID(noteDocument.getId().getNoteID());
        dto.setDocID(noteDocument.getId().getDocID());
        dto.setDocument(noteDocument.getDocument());
        return dto;
    }
}
