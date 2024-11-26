package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.CarePlanLongTermGoalDTO;
import com.skycareSolutions.skyCarePortal.service.CarePlanLongTermGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/long-term-plans")
public class CarePlanLongTermGoalController {

    @Autowired
    private CarePlanLongTermGoalService service;

    // Create or Update CarePlanLongTermGoal
    @PostMapping
    public ResponseEntity<CarePlanLongTermGoalDTO> createOrUpdate(@RequestBody CarePlanLongTermGoalDTO dto) {
        return ResponseEntity.ok(service.saveOrUpdate(dto));
    }

    // Get CarePlanLongTermGoal by ID
    @GetMapping("/{id}")
    public ResponseEntity<CarePlanLongTermGoalDTO> getById(@PathVariable String id) {
        Optional<CarePlanLongTermGoalDTO> carePlan = service.findById(id);
        return carePlan.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Get All CarePlanLongTermGoals
    @GetMapping
    public ResponseEntity<List<CarePlanLongTermGoalDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Delete CarePlanLongTermGoal by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        service.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
