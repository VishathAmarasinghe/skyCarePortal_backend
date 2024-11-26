package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.CarePlanShortTermGoalDTO;
import com.skycareSolutions.skyCarePortal.service.CarePlanShortTermGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/short-term-plans")
public class CarePlanShortTermGoalController {

    @Autowired
    private CarePlanShortTermGoalService service;

    // Create or Update CarePlanShortTermGoal
    @PostMapping
    public ResponseEntity<CarePlanShortTermGoalDTO> createOrUpdate(@RequestBody CarePlanShortTermGoalDTO dto) {
        return ResponseEntity.ok(service.saveOrUpdate(dto));
    }

    // Get CarePlanShortTermGoal by ID
    @GetMapping("/{id}")
    public ResponseEntity<CarePlanShortTermGoalDTO> getById(@PathVariable String id) {
        Optional<CarePlanShortTermGoalDTO> carePlan = service.findById(id);
        return carePlan.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Get All CarePlanShortTermGoals
    @GetMapping
    public ResponseEntity<List<CarePlanShortTermGoalDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Delete CarePlanShortTermGoal by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        service.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
