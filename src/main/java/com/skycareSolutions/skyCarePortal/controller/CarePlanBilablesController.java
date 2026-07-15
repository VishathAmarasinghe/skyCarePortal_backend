package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.CarePlanBilablesDTO;
import com.skycareSolutions.skyCarePortal.dto.CarePlanBillableWithCarePlanDTO;
import com.skycareSolutions.skyCarePortal.model.CarePlan;
import com.skycareSolutions.skyCarePortal.repository.CarePlanRepository;
import com.skycareSolutions.skyCarePortal.service.CarePlanBillablesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/care-plan-bills")
public class CarePlanBilablesController {

    @Autowired
    private CarePlanBillablesService carePlanBillablesService;

    @Autowired
    private CarePlanRepository carePlanRepository;


    @GetMapping("/{id}")
    public ResponseEntity<CarePlanBilablesDTO> getBillById(@PathVariable String id) {
        return ResponseEntity.ok(carePlanBillablesService.getBillById(id));
    }

    @GetMapping
    public ResponseEntity<List<CarePlanBillableWithCarePlanDTO>> getAllCarePlanBillablesWithCarePlan(){
        return ResponseEntity.ok(carePlanBillablesService.getAllCarePlanBills());
    }

    @GetMapping("/by-careplan/{carePlanID}")
    public ResponseEntity<List<CarePlanBilablesDTO>> getBillsByCarePlanID(@PathVariable String carePlanID) {
        CarePlan carePlan = carePlanRepository.findById(carePlanID)
                .orElseThrow(() -> new RuntimeException("CarePlan not found with ID: " + carePlanID));
        return ResponseEntity.ok(carePlanBillablesService.getBillsByCarePlanID(carePlan));
    }

    @PostMapping
    public ResponseEntity<CarePlanBilablesDTO> createCarePlanBill(@RequestBody CarePlanBilablesDTO billDTO) {
        CarePlanBilablesDTO created = carePlanBillablesService.createCarePlanBill(billDTO);
        URI location = URI.create(String.format("/care-plan-bills/%s", created.getBillID()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarePlanBilablesDTO> updateCarePlanBill(@PathVariable String id, @RequestBody CarePlanBilablesDTO billDTO) {
        return ResponseEntity.ok(carePlanBillablesService.updateCarePlanBill(id, billDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarePlanBill(@PathVariable String id) {
        return carePlanBillablesService.deleteCarePlanBill(id);
    }
}
