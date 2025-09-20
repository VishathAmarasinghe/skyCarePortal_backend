package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.SignatureDTO;
import com.skycareSolutions.skyCarePortal.service.SignatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
@RequestMapping("/signatures")
public class SignatureController {

    private static final Logger logger = LoggerFactory.getLogger(SignatureController.class);

    @Autowired
    private SignatureService signatureService;

    // Upload/Save signature for an employee
    @PostMapping("/upload/{employeeID}")
    public ResponseEntity<ApiResponse> uploadSignature(
            @PathVariable String employeeID,
            @RequestParam("signature") MultipartFile signatureFile) {
        
        try {
            logger.info("Uploading signature for employee: {}", employeeID);
            
            signatureService.saveSignature(employeeID, signatureFile);
            
            return new ResponseEntity<>(
                new ApiResponse("Signature uploaded successfully for employee: " + employeeID, true),
                HttpStatus.CREATED
            );
            
        } catch (Exception e) {
            logger.error("Error uploading signature for employee {}: {}", employeeID, e.getMessage());
            return new ResponseEntity<>(
                new ApiResponse("Failed to upload signature: " + e.getMessage(), false),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    // Get signature by employee ID
    @GetMapping("/employee/{employeeID}")
    public ResponseEntity<?> getSignatureByEmployeeID(@PathVariable String employeeID) {
        try {
            logger.info("Retrieving signature for employee: {}", employeeID);
            
            SignatureDTO signatureDTO = signatureService.getSignatureByEmployeeID(employeeID);
            
            return new ResponseEntity<>(signatureDTO, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error retrieving signature for employee {}: {}", employeeID, e.getMessage());
            return new ResponseEntity<>(
                new ApiResponse("Failed to retrieve signature: " + e.getMessage(), false),
                HttpStatus.NOT_FOUND
            );
        }
    }

    // Update signature for an employee
    @PutMapping("/update/{employeeID}")
    public ResponseEntity<ApiResponse> updateSignature(
            @PathVariable String employeeID,
            @RequestParam("signature") MultipartFile signatureFile) {
        
        try {
            logger.info("Updating signature for employee: {}", employeeID);
            
            signatureService.updateSignature(employeeID, signatureFile);
            
            return new ResponseEntity<>(
                new ApiResponse("Signature updated successfully for employee: " + employeeID, true),
                HttpStatus.OK
            );
            
        } catch (Exception e) {
            logger.error("Error updating signature for employee {}: {}", employeeID, e.getMessage());
            return new ResponseEntity<>(
                new ApiResponse("Failed to update signature: " + e.getMessage(), false),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    // Delete signature by employee ID
    @DeleteMapping("/employee/{employeeID}")
    public ResponseEntity<ApiResponse> deleteSignatureByEmployeeID(@PathVariable String employeeID) {
        try {
            logger.info("Deleting signature for employee: {}", employeeID);
            
            boolean deleted = signatureService.deleteSignatureByEmployeeID(employeeID);
            
            if (deleted) {
                return new ResponseEntity<>(
                    new ApiResponse("Signature deleted successfully for employee: " + employeeID, true),
                    HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(
                    new ApiResponse("No signature found for employee: " + employeeID, false),
                    HttpStatus.NOT_FOUND
                );
            }
            
        } catch (Exception e) {
            logger.error("Error deleting signature for employee {}: {}", employeeID, e.getMessage());
            return new ResponseEntity<>(
                new ApiResponse("Failed to delete signature: " + e.getMessage(), false),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Check if signature exists for employee
    @GetMapping("/exists/{employeeID}")
    public ResponseEntity<?> checkSignatureExists(@PathVariable String employeeID) {
        try {
            logger.info("Checking if signature exists for employee: {}", employeeID);
            
            boolean exists = signatureService.signatureExistsByEmployeeID(employeeID);
            
            return new ResponseEntity<>(
                new ApiResponse("Signature exists: " + exists, exists),
                HttpStatus.OK
            );
            
        } catch (Exception e) {
            logger.error("Error checking signature existence for employee {}: {}", employeeID, e.getMessage());
            return new ResponseEntity<>(
                new ApiResponse("Failed to check signature existence: " + e.getMessage(), false),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
