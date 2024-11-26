package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.GoalOutcomeDTO;
import com.skycareSolutions.skyCarePortal.service.GoalOutcomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/goal-outcome")
public class GoalOutComeController {

    @Autowired
    private GoalOutcomeService service;

    // Create or Update a GoalOutcome
    @PostMapping
    public ResponseEntity<ApiResponse> saveOrUpdate(@RequestBody GoalOutcomeDTO dto) {
        try {
            service.saveOrUpdate(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Goal outcome created",true));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Get GoalOutcome by ID
    @GetMapping("/{id}")
    public GoalOutcomeDTO findById(@PathVariable String id) {
        return service.findById(id);
    }

    // Get All GoalOutcomes
    @GetMapping
    public List<GoalOutcomeDTO> findAll() {
        return service.findAll();
    }

    // Delete GoalOutcome by ID
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        service.deleteById(id);
    }
}
