package com.skycareSolutions.skyCarePortal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.exception.TimesheetComplianceException;
import com.skycareSolutions.skyCarePortal.service.ShiftNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin("*")
@RequestMapping("/shift-notes")

public class ShiftNoteController {

    private final ObjectMapper objectMapper;

    public ShiftNoteController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Autowired
    private ShiftNoteService shiftNoteService;

    private ResponseEntity<String> complianceBlockedResponse(TimesheetComplianceException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    }

    @PostMapping("/start")
    public ResponseEntity<?> startShift(@RequestBody StartShiftNoteDTO startShiftNoteDTO) {
        try {
            Boolean isShiftStarted = shiftNoteService.startShift(startShiftNoteDTO);
            if (isShiftStarted) {
                return new ResponseEntity<>(true, HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (TimesheetComplianceException exception) {
            return complianceBlockedResponse(exception);
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> newShiftNote(
            @RequestPart("note") String shiftNoteDTO,
            @RequestPart(value = "files",required = false) List<MultipartFile> files
    ) {
        try {
            ShiftNoteDTO notes = objectMapper.readValue(shiftNoteDTO,ShiftNoteDTO.class);
            return ResponseEntity.ok(shiftNoteService.newShiftNotes(notes,files));
        } catch (TimesheetComplianceException exception) {
            return complianceBlockedResponse(exception);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("duplicate")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", e.getMessage());
                errorResponse.put("duplicate", true);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
            throw e;
        } catch (Exception e){
            System.out.println("exception "+e);
            return ResponseEntity.internalServerError().body(false);
        }
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
    public ResponseEntity<?> saveFinishDetails(
            @RequestPart("note") String shiftNoteDTO,
            @RequestPart(value = "files",required = false) List<MultipartFile> files) {
        try {
            ShiftNoteDTO notes = objectMapper.readValue(shiftNoteDTO,ShiftNoteDTO.class);
            shiftNoteService.finishShift(notes,files);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Shift Added successfully",true));
        } catch (TimesheetComplianceException exception) {
            return complianceBlockedResponse(exception);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("duplicate")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(), false));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),false));
        }
    }

    @GetMapping
    public ResponseEntity<List<ShiftNoteDTO>> getAllShiftNotes(){
        return ResponseEntity.ok(shiftNoteService.getAllShiftNotes());
    }

    @GetMapping("/time-sheets")
    public ResponseEntity<List<TimeSheetDTO>> getTimeSheetsWithinDateRange(@RequestParam String startDate,@RequestParam String endDate,@RequestParam String careGiverID,@RequestParam String clientID){
        return ResponseEntity.ok(shiftNoteService.getTimeSheetsForGivenDateRange(startDate,endDate,careGiverID,clientID));
    }

    @PutMapping("/time-sheets/status/{id}")
    public ResponseEntity<String> updateTimeSheetPaymentState(@PathVariable String id,@RequestParam String status,@RequestBody Map<String, String> requestBody){
        String comment = requestBody.get("comment");
        return ResponseEntity.ok(shiftNoteService.updateShiftNotePaymentStatus(id,status,comment));
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
    public ResponseEntity<?> updateShiftNote(
            @PathVariable String noteID,
            @RequestPart("note") String shiftNoteDTO,
            @RequestPart(value = "files",required = false) List<MultipartFile> files) {
        try {
            ShiftNoteDTO notes = objectMapper.readValue(shiftNoteDTO,ShiftNoteDTO.class);
            ShiftNoteDTO updatedShiftNote = shiftNoteService.updateShiftNote(noteID, notes, files);
            return ResponseEntity.ok(updatedShiftNote);
        } catch (TimesheetComplianceException exception) {
            return complianceBlockedResponse(exception);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("duplicate")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", e.getMessage());
                errorResponse.put("duplicate", true);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
            System.out.println("issue found "+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            System.out.println("issue found "+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/export/increment")
    public ResponseEntity<Integer> incrementExportCount(@RequestBody List<String> noteIds){
        int updated = shiftNoteService.incrementExportCount(noteIds);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/export/decrement")
    public ResponseEntity<Integer> decrementExportCount(@RequestBody List<String> noteIds){
        int updated = shiftNoteService.decrementExportCount(noteIds);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/export/bulk-update")
    public ResponseEntity<Map<String, Integer>> bulkUpdateExportCount(
            @RequestBody Map<String, Object> request){
        List<String> noteIds = (List<String>) request.get("noteIds");
        String action = (String) request.get("action");
        
        Map<String, Integer> result = shiftNoteService.bulkUpdateExportCount(noteIds, action);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/export/time-sheets")
    public ResponseEntity<List<TimeSheetDTO>> getExportableTimeSheets(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String careGiverID,
            @RequestParam String clientID
    ){
        return ResponseEntity.ok(
                shiftNoteService.getTimeSheetsForGivenDateRangeForExport(startDate,endDate,careGiverID,clientID)
        );
    }

    @PostMapping("/time-sheets/bulk-status-update")
    public ResponseEntity<Map<String, Integer>> bulkUpdatePaymentStatus(
            @RequestBody Map<String, Object> request){
        List<String> noteIds = (List<String>) request.get("noteIds");
        String status = (String) request.get("status");
        String comment = (String) request.get("comment");
        
        Map<String, Integer> result = shiftNoteService.bulkUpdatePaymentStatus(noteIds, status, comment);
        return ResponseEntity.ok(result);
    }
}
