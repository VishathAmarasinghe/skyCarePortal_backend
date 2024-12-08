package com.skycareSolutions.skyCarePortal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skycareSolutions.skyCarePortal.dto.AppointmentCalenderDTO;
import com.skycareSolutions.skyCarePortal.dto.AppointmentDTO;
import com.skycareSolutions.skyCarePortal.dto.CareGiverAppointmentDTO;
import com.skycareSolutions.skyCarePortal.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    private final ObjectMapper objectMapper;

    public AppointmentController() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Register the JSR310 module
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/calender")
    public ResponseEntity<List<AppointmentCalenderDTO>> getCalenderEvents(
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate
            ){
        return ResponseEntity.ok(appointmentService.getAllEventsRelatedToDateRange(startDate,endDate));
    }

    @GetMapping("/client/{clientID}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByClientID(@PathVariable String clientID){
        return ResponseEntity.ok(appointmentService.getAppointmentsbyClientID(clientID));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable String id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(
            @RequestPart(value = "appointment") String appointmentString,
            @RequestPart(value = "files",required = false) List<MultipartFile> files) {
        try {
            AppointmentDTO appointmentDTO = objectMapper.readValue(appointmentString, AppointmentDTO.class);
            return ResponseEntity.ok(appointmentService.createAppointment(appointmentDTO,files));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointment(
            @PathVariable String id,
            @RequestPart(value = "appointment") String appointmentString,
            @RequestPart(value = "files",required = false) List<MultipartFile> files) {

        try {
            System.out.println("id is "+id);
            AppointmentDTO appointmentDTO = objectMapper.readValue(appointmentString, AppointmentDTO.class);
            return ResponseEntity.ok(appointmentService.updateAppointment(id,appointmentDTO,files));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/careGiver/{employeeID}")
    public ResponseEntity<List<CareGiverAppointmentDTO>> getAppointmentsAssignedToCareGiver(
            @PathVariable(name = "employeeID") String employeeID,
            @RequestParam(name = "status") String status
    ){
        try {
            return ResponseEntity.ok(appointmentService.getAppointmentsAssignedToCareGiver(employeeID,status));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
