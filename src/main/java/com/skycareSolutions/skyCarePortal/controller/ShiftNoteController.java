package com.skycareSolutions.skyCarePortal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.service.ResourceService;
import com.skycareSolutions.skyCarePortal.service.ShiftNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/shift-notes")

public class ShiftNoteController {

    private final ObjectMapper objectMapper;

    public ShiftNoteController(ObjectMapper objectMapper) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Autowired
    private ShiftNoteService shiftNoteService;

    @PostMapping("/start")
    public ResponseEntity<Boolean> startShift(@RequestBody StartShiftNoteDTO startShiftNoteDTO) {
        // Call the service method to start the shift
        Boolean isShiftStarted = shiftNoteService.startShift(startShiftNoteDTO);

        // If shift is successfully started, return the ShiftNoteDTO
        if (isShiftStarted) {
            return new ResponseEntity<>(true, HttpStatus.CREATED); // 201 Created
        }

        // If shift start failed, return a bad request response
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
    }
    @GetMapping("/shift-note-state")
    public ResponseEntity<ShiftNoteStateDTO> getShiftNoteState(@RequestParam(name = "recurrentAppointmentID") String recurrentAppointmentID
    , @RequestParam(name = "employeeID") String employeeID){
        StartShiftNoteDTO startShiftNoteDTO = new StartShiftNoteDTO();
        startShiftNoteDTO.setEmployeeID(employeeID);
        startShiftNoteDTO.setRecurrentAppointmentID(recurrentAppointmentID);
        return ResponseEntity.ok(shiftNoteService.getShiftNoteState(startShiftNoteDTO));
    }

    @PostMapping("/finish")
    public ResponseEntity<ApiResponse> saveFinishDetails(
            @RequestPart("note") String shiftNoteDTO,
            @RequestPart(value = "files",required = false) List<MultipartFile> files) {
        try {
            ShiftNoteDTO notes = objectMapper.readValue(shiftNoteDTO,ShiftNoteDTO.class);
            shiftNoteService.finishShift(notes,files);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Shift Added successfully",true));
        }catch (Exception e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),false));
        }
    }

    @GetMapping
    public ResponseEntity<List<ShiftNoteDTO>> getAllShiftNotes(){
        return ResponseEntity.ok(shiftNoteService.getAllShiftNotes());
    }

    @GetMapping("/careGiver/{id}")
    public ResponseEntity<List<ShiftNoteDTO>> getAllShiftNotesRelatedToEmployee(@PathVariable String id){
        return ResponseEntity.ok(shiftNoteService.getAllShiftNotesRelatedToCareGiver(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftNoteDTO> getSingleShiftNote(@PathVariable String id){
        return ResponseEntity.ok(shiftNoteService.getShiftNote(id));
    }

    @PutMapping("/{noteID}")
    public ResponseEntity<ShiftNoteDTO> updateShiftNote(
            @PathVariable String noteID,
            @RequestPart("note") String shiftNoteDTO,
            @RequestPart(value = "files",required = false) List<MultipartFile> files) {
        try {
            ShiftNoteDTO notes = objectMapper.readValue(shiftNoteDTO,ShiftNoteDTO.class);
            ShiftNoteDTO updatedShiftNote = shiftNoteService.updateShiftNote(noteID, notes, files);
            return ResponseEntity.ok(updatedShiftNote);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
