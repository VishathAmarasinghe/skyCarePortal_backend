package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.CarePlanDTO;
import com.skycareSolutions.skyCarePortal.service.CarePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/careplans")
public class CarePlanController {

    @Autowired
    private CarePlanService carePlanService;

    // Get CarePlans by clientID
    @GetMapping("/client/{clientID}")
    public ResponseEntity<List<CarePlanDTO>> getCarePlansByClientID(@PathVariable String clientID) {
        List<CarePlanDTO> carePlans = carePlanService.getCarePlansByClientID(clientID);
        return ResponseEntity.ok(carePlans); // 200 OK
    }

    // Get all CarePlans
    @GetMapping
    public ResponseEntity<List<CarePlanDTO>> getAllCarePlans() {
        List<CarePlanDTO> carePlans = carePlanService.getAllCarePlans();
        return ResponseEntity.ok(carePlans); // 200 OK
    }

    // Create a new CarePlan
    @PostMapping
    public ResponseEntity<ApiResponse> createCarePlan(@RequestBody CarePlanDTO carePlanDTO) {
        CarePlanDTO createdCarePlan = carePlanService.createCarePlan(carePlanDTO);
        return new ResponseEntity<>(new ApiResponse("Care Plan Created successfully",true), HttpStatus.CREATED); // 201 Created
    }

    // Update a CarePlan
    @PutMapping("/{id}")
    public ResponseEntity<CarePlanDTO> updateCarePlan(@PathVariable String id, @RequestBody CarePlanDTO carePlanDTO) {
        CarePlanDTO updatedCarePlan = carePlanService.updateCarePlan(id, carePlanDTO);
        return ResponseEntity.ok(updatedCarePlan); // 200 OK
    }

    // Delete a CarePlan
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarePlan(@PathVariable String id) {
        carePlanService.deleteCarePlan(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
