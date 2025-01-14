package com.skycareSolutions.skyCarePortal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.service.AppointmentService;
import com.skycareSolutions.skyCarePortal.service.RecurrenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    @Autowired
    @Lazy
    private RecurrenceService recurrenceService;

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

    @GetMapping("/pending/{id}")
    public ResponseEntity<List<PendingAppointmentDTO>> getPendingAppointmentsRelatedToCareGiver(
            @PathVariable String id
    ){
        return ResponseEntity.ok(appointmentService.getPendingAppointmentsRelatedToUser(id));
    }

    @GetMapping("/calender/user/{id}")
    public ResponseEntity<List<AppointmentCalenderDTO>> getCalenderEventsByUser(
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate,
            @PathVariable String id
    ){

        return ResponseEntity.ok(appointmentService.getAllEventsRelatedToDateRangeWithUser(id,startDate,endDate));
    }

    @GetMapping("/recurrentAppointment/{id}")
    public ResponseEntity<PendingAppointmentDTO> getSelectedRecurrentAppointment(@PathVariable String id){
        return  ResponseEntity.ok(appointmentService.getSelectedRecurrentAppointment(id));
    }

    @PostMapping("/recurrentAppointment")
    public ResponseEntity updateRecurrentAppointment(
            @RequestBody RecurrentAppointmentUpdateDTO recurrentAppointmentUpdateDTO){
        return ResponseEntity.ok(recurrenceService.updateRecurrentAppointment(recurrentAppointmentUpdateDTO));
    }

    @GetMapping("/client/{clientID}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByClientID(@PathVariable String clientID){
        return ResponseEntity.ok(appointmentService.getAppointmentsbyClientID(clientID));
    }

    @DeleteMapping("/cancelAppointment/{id}")
    public ResponseEntity<Boolean> cancelAppointment(@PathVariable String id){
        return ResponseEntity.ok(recurrenceService.cancelAppointment(id));
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

    @GetMapping("/next/appointment/{id}")
    public ResponseEntity<NextAppointmentCareGiverDTO> getNextAppointmentToCareGiver(
            @PathVariable String id
    )
    {
        return ResponseEntity.ok(appointmentService.getNextAssignedAppointmentToCareGiver(id));
    }

    @GetMapping("/careGiver/{employeeID}")
    public ResponseEntity<List<CareGiverAllocatedAppointmentsDTO>> getAppointmentsAssignedToCareGiver(
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
