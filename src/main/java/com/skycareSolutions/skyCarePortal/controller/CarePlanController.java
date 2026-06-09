package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.CarePlanDTO;
import com.skycareSolutions.skyCarePortal.service.CarePlanService;
import com.skycareSolutions.skyCarePortal.service.NdisSupportPlanPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    @Autowired
    private NdisSupportPlanPdfService ndisSupportPlanPdfService;



    // Get CarePlans by clientID
    @GetMapping("/client/{clientID}")
    public ResponseEntity<List<CarePlanDTO>> getCarePlansByClientID(@PathVariable String clientID) {
        List<CarePlanDTO> carePlans = carePlanService.getCarePlansByClientID(clientID);
        return ResponseEntity.ok(carePlans); // 200 OK
    }

    @GetMapping("/deactivate/{id}")
    public ResponseEntity<String> deactivateCarePlan(@PathVariable String id) {
        carePlanService.deactivateCarePlan(id);
        return ResponseEntity.ok("Deleted"); // 200 OK
    }

    @GetMapping("/{carePlanID}")
    public ResponseEntity<CarePlanDTO> getCarePlansByPlanID(@PathVariable String carePlanID) {
        CarePlanDTO carePlan = carePlanService.getCarePlansByCarePlanID(carePlanID);
        return ResponseEntity.ok(carePlan); // 200 OK
    }

    @GetMapping(value = "/{carePlanID}/ndis-support-plan.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadNdisSupportPlanPdf(@PathVariable String carePlanID) {
        byte[] pdfBytes = ndisSupportPlanPdfService.generateNdisSupportPlanPdfBytes(carePlanID);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"NDIS-Support-Plan-" + carePlanID + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
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
        carePlanService.createCarePlan(carePlanDTO);
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
