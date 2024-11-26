package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.AppointmentTypeDTO;
import com.skycareSolutions.skyCarePortal.service.AppointmentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/appointment-types")
public class AppointmentTypesController {

    @Autowired
    private AppointmentTypeService appointmentTypeService;

    @GetMapping
    public ResponseEntity<List<AppointmentTypeDTO>> getAllAppointmentTypes() {
        return ResponseEntity.ok(appointmentTypeService.getAllAppointmentTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentTypeDTO> getAppointmentTypeById(@PathVariable String id) {
        return ResponseEntity.ok(appointmentTypeService.getAppointmentTypeById(id));
    }

    @PostMapping
    public ResponseEntity<AppointmentTypeDTO> createAppointmentType(@RequestBody AppointmentTypeDTO appointmentTypeDTO) {
        return ResponseEntity.ok(appointmentTypeService.createAppointmentType(appointmentTypeDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentTypeDTO> updateAppointmentType(
            @PathVariable String id, @RequestBody AppointmentTypeDTO appointmentTypeDTO) {
        return ResponseEntity.ok(appointmentTypeService.updateAppointmentType(id, appointmentTypeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointmentType(@PathVariable String id) {
        appointmentTypeService.deleteAppointmentType(id);
        return ResponseEntity.noContent().build();
    }
}
