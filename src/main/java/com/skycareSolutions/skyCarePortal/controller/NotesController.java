package com.skycareSolutions.skyCarePortal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.NotesDTO;
import com.skycareSolutions.skyCarePortal.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
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

    @PostMapping()
    public ResponseEntity<ApiResponse> createNote(
            @RequestPart("note") String notesDTO,
            @RequestPart(value = "files") List<MultipartFile> files) {
        System.out.println("functionality came here ");
        try {
            ObjectMapper mapper = new ObjectMapper();
            NotesDTO notes = mapper.readValue(notesDTO,NotesDTO.class);
            notesService.createNote(notes,files);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Notes Added successfully",true));
        }catch (Exception e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),false));
        }
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
