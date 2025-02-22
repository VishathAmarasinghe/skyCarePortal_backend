package com.skycareSolutions.skyCarePortal.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skycareSolutions.skyCarePortal.dto.AppointmentDTO;
import com.skycareSolutions.skyCarePortal.dto.CareGiverDTO;
import com.skycareSolutions.skyCarePortal.dto.CareGiverDocumentsDTO;
import com.skycareSolutions.skyCarePortal.service.CareGiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/care-givers")
public class CareGiverController {

    private final ObjectMapper objectMapper;

    public CareGiverController() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Register the JSR310 module
    }

    @Autowired
    private CareGiverService careGiverService;

    @GetMapping
    public ResponseEntity<List<CareGiverDTO>> getAllCareGivers() {
        return ResponseEntity.ok(careGiverService.getAllCareGivers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CareGiverDTO> getCareGiverById(@PathVariable String id) {
        return careGiverService.getCareGiverById(id);
    }

    @GetMapping("/employee")
    public ResponseEntity<CareGiverDTO> getCareGiverByEmployeeID(@RequestParam("employeeID") String employeeID) {
        return ResponseEntity.ok(careGiverService.getCareGiverByEmployee(employeeID));
    }

    @PostMapping
    public ResponseEntity<CareGiverDTO> addCareGiver(
            @RequestPart("careGiver") String careGiverData,
            @RequestParam(value = "profile_photo",required = false) MultipartFile profilePhoto,
            @RequestParam(value = "documents",required = false) List<MultipartFile> documents) {
        try {
            CareGiverDTO careGiverDTO = objectMapper.readValue(careGiverData, CareGiverDTO.class);
            return careGiverService.addCareGiver(careGiverDTO,profilePhoto,documents);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CareGiverDTO> updateCareGiver(
            @PathVariable String id,
            @RequestPart("careGiver") String careGiverData,
            @RequestParam(value = "profile_photo",required = false) MultipartFile profilePhoto,
            @RequestParam(value = "documents",required = false) List<MultipartFile> documents) {
        try{
            CareGiverDTO careGiverDTO = objectMapper.readValue(careGiverData, CareGiverDTO.class);
            return careGiverService.updateCareGiver(id, careGiverDTO,profilePhoto,documents);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/documents/{id}")
    public ResponseEntity<String> updateCareGiverDocuments(
            @PathVariable String id,
            @RequestPart("careGiver") String careGiverData,
            @RequestPart(value = "files",required = false) List<MultipartFile> files) {
        try {
            CareGiverDTO careGiverDetails = objectMapper.readValue(careGiverData, CareGiverDTO.class);
            for (CareGiverDocumentsDTO doc:careGiverDetails.getCareGiverDocuments()){
                System.out.println("care Giver doc expo date "+doc.getExpDate());
            }
            return careGiverService.updateCareGiverDocuments(id, careGiverDetails.getCareGiverDocuments(), files);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse careGiverDocuments", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCareGiver(@PathVariable String id) {
        return careGiverService.deleteCareGiver(id);
    }
}
