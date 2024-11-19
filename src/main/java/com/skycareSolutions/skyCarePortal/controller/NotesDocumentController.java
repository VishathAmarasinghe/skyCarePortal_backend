package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.NoteDocumentsDTO;
import com.skycareSolutions.skyCarePortal.service.NotesDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/note-documents")
public class NotesDocumentController {

    @Autowired
    private NotesDocumentService notesDocumentService;

    // Create or Update a document
    @PostMapping
    public ResponseEntity<NoteDocumentsDTO> saveNoteDocument(@RequestBody NoteDocumentsDTO noteDocumentsDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notesDocumentService.saveNoteDocument(noteDocumentsDTO));
    }

    // Get all documents
    @GetMapping
    public ResponseEntity<List<NoteDocumentsDTO>> getAllNoteDocuments() {
        return ResponseEntity.ok(notesDocumentService.getAllNoteDocuments());
    }

    // Get documents by noteID
    @GetMapping("/note/{noteID}")
    public ResponseEntity<List<NoteDocumentsDTO>> getDocumentsByNoteID(@PathVariable String noteID) {
        return ResponseEntity.ok(notesDocumentService.getDocumentsByNoteID(noteID));
    }

    // Delete a document by composite key
    @DeleteMapping("/{noteID}/{docID}")
    public ResponseEntity<String> deleteNoteDocument(@PathVariable String noteID, @PathVariable String docID) {
        notesDocumentService.deleteNoteDocument(noteID, docID);
        return ResponseEntity.ok("Document deleted successfully");
    }

    // Delete all documents by noteID
    @DeleteMapping("/note/{noteID}")
    public ResponseEntity<String> deleteDocumentsByNoteID(@PathVariable String noteID) {
        notesDocumentService.deleteDocumentsByNoteID(noteID);
        return ResponseEntity.ok("All documents for the note deleted successfully");
    }
}
