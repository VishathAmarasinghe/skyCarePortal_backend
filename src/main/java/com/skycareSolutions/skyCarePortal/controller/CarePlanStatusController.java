package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.CarePlanStatusDTO;
import com.skycareSolutions.skyCarePortal.service.CarePlanStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/careplan-status")
public class CarePlanStatusController {

    @Autowired
    private CarePlanStatusService carePlanStatusService;

    @GetMapping
    public ResponseEntity<List<CarePlanStatusDTO>> getAllCarePlanStatuses() {
        return ResponseEntity.ok(carePlanStatusService.getAllCarePlanStatuses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarePlanStatusDTO> getCarePlanStatusById(@PathVariable String id) {
        return ResponseEntity.ok(carePlanStatusService.getCarePlanStatusById(id));
    }

    @PostMapping
    public ResponseEntity<CarePlanStatusDTO> createCarePlanStatus(@RequestBody CarePlanStatusDTO carePlanStatusDTO) {
        CarePlanStatusDTO created = carePlanStatusService.createCarePlanStatus(carePlanStatusDTO);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarePlanStatusDTO> updateCarePlanStatus(@PathVariable String id, @RequestBody CarePlanStatusDTO carePlanStatusDTO) {
        CarePlanStatusDTO updated = carePlanStatusService.updateCarePlanStatus(id, carePlanStatusDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarePlanStatus(@PathVariable String id) {
        return carePlanStatusService.deleteCarePlanStatus(id);
    }
}
