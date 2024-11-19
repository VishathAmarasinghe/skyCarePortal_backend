package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.NotesDTO;
import com.skycareSolutions.skyCarePortal.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/notes")
public class NotesController {

    @Autowired
    private NotesService notesService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> createNote(
            @RequestPart("note") NotesDTO notesDTO,
            @RequestPart("files") List<MultipartFile> files) {
        notesService.createNote(notesDTO,files);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Notes Added successfully",true));
    }

    @PutMapping("/{noteID}")
    public ResponseEntity<NotesDTO> updateNote(@PathVariable String noteID, @RequestBody NotesDTO notesDTO) {
        return ResponseEntity.ok(notesService.updateNote(noteID, notesDTO));
    }

    @GetMapping
    public ResponseEntity<List<NotesDTO>> getAllNotes() {
        return ResponseEntity.ok(notesService.getAllNotes());
    }

    @GetMapping("/{noteID}")
    public ResponseEntity<NotesDTO> getNoteById(@PathVariable String noteID) {
        return ResponseEntity.ok(notesService.getNoteById(noteID));
    }

    @GetMapping("/client/{clientID}")
    public ResponseEntity<List<NotesDTO>> getNotesByClientId(@PathVariable String clientID) {
        return ResponseEntity.ok(notesService.getNotesByClientId(clientID));
    }

    @DeleteMapping("/{noteID}")
    public ResponseEntity<String> deleteNoteById(@PathVariable String noteID) {
        notesService.deleteNoteById(noteID);
        return ResponseEntity.ok("Note deleted successfully");
    }
}
