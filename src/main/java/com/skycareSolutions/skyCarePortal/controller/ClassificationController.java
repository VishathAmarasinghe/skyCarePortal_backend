package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.ClassificationDTO;
import com.skycareSolutions.skyCarePortal.service.ClassificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/classifications")
public class ClassificationController {

    private static final Logger logger = LoggerFactory.getLogger(ClassificationController.class);


    final private ClassificationService classificationService;

    public ClassificationController(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    // Create a new classification
    @PostMapping
    public ResponseEntity<ApiResponse> createClassification(@RequestBody ClassificationDTO classificationDTO) {
        try {
            classificationService.createClassification(classificationDTO);
            return new ResponseEntity<>(new ApiResponse("Created classification successfully",true), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unable to create classification",false), HttpStatus.BAD_REQUEST);
        }
    }

    // Get all classifications
    @GetMapping
    public List<ClassificationDTO> getAllClassifications() {
        return classificationService.getAllClassifications();
    }

    // Get a specific classification by its name
    @GetMapping("/getByName/{name}")
    public ResponseEntity<ClassificationDTO> getClassificationByName(@PathVariable String name) {
        ClassificationDTO classification = classificationService.getClassificationByName(name);
        return classification != null ? new ResponseEntity<>(classification, HttpStatus.OK) :
                new ResponseEntity<>(null,HttpStatus.OK);
    }


    // Get a specific classification by its name
    @GetMapping("/getById/{id}")
    public ResponseEntity<ClassificationDTO> getClassificationById(@PathVariable String id) {
        System.out.println("value came here"+id);
        ClassificationDTO classification = classificationService.getClassificationByID(id);
        return classification != null ? new ResponseEntity<>(classification, HttpStatus.OK) :
                new ResponseEntity<>(null,HttpStatus.OK);
    }

    // Update an existing classification
    @PutMapping("/{id}")
    public ResponseEntity<String> updateClassification(@PathVariable String id, @RequestBody ClassificationDTO classificationDTO) {
        try {
            classificationService.updateClassification(id, classificationDTO);
            return new ResponseEntity<>("Classification updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete a classification by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClassification(@PathVariable String id) {
        try {
            classificationService.deleteClassification(id);
            return new ResponseEntity<>("Classification deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
