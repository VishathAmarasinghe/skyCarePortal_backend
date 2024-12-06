package com.skycareSolutions.skyCarePortal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skycareSolutions.skyCarePortal.dto.AppointmentDTO;
import com.skycareSolutions.skyCarePortal.dto.CareGiverDTO;
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
        return careGiverService.getAllCareGivers();
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
            @RequestParam("documents") List<MultipartFile> documents) {
        try {
            CareGiverDTO careGiverDTO = objectMapper.readValue(careGiverData, CareGiverDTO.class);
            return careGiverService.addCareGiver(careGiverDTO,profilePhoto,documents);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CareGiverDTO> updateCareGiver(@PathVariable String id, @RequestBody CareGiverDTO dto) {
        return careGiverService.updateCareGiver(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCareGiver(@PathVariable String id) {
        return careGiverService.deleteCareGiver(id);
    }
}
