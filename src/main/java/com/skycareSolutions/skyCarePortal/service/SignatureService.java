package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.SignatureDTO;
import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.model.Signature;
import com.skycareSolutions.skyCarePortal.repository.EmployeeRepository;
import com.skycareSolutions.skyCarePortal.repository.SignatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class SignatureService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "Signatures").toString();

    @Autowired
    private SignatureRepository signatureRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // Save or update signature for an employee
    public SignatureDTO saveSignature(String employeeID, MultipartFile signatureFile) {
        try {
            // Validate employee exists
            Optional<Employee> employeeOptional = employeeRepository.findById(employeeID);
            if (employeeOptional.isEmpty()) {
                throw new RuntimeException("Employee with ID " + employeeID + " not found");
            }

            Employee employee = employeeOptional.get();

            // Check if signature already exists
            Optional<Signature> existingSignature = signatureRepository.findByEmployeeID(employeeID);
            Signature signature;

            if (existingSignature.isPresent()) {
                // Update existing signature
                signature = existingSignature.get();
            } else {
                // Create new signature
                signature = new Signature();
                signature.setEmployeeID(employeeID);
            }

            // Save the file
            String fileName = saveSignatureFile(employeeID, signatureFile);
            signature.setSignature(fileName);

            // Save to database
            Signature savedSignature = signatureRepository.save(signature);

            // Convert to DTO and return
            return convertToDTO(savedSignature, employee);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save signature: " + e.getMessage(), e);
        }
    }

    // Get signature by employee ID
    public SignatureDTO getSignatureByEmployeeID(String employeeID) {
        Optional<Signature> signatureOptional = signatureRepository.findByEmployeeID(employeeID);
        
        if (signatureOptional.isEmpty()) {
            throw new RuntimeException("No signature found for employee ID: " + employeeID);
        }

        Optional<Employee> employeeOptional = employeeRepository.findById(employeeID);
        if (employeeOptional.isEmpty()) {
            throw new RuntimeException("Employee with ID " + employeeID + " not found");
        }

        return convertToDTO(signatureOptional.get(), employeeOptional.get());
    }

    // Update signature by employee ID
    public SignatureDTO updateSignature(String employeeID, MultipartFile signatureFile) {
        return saveSignature(employeeID, signatureFile); // Same logic as save
    }

    // Delete signature by employee ID
    public boolean deleteSignatureByEmployeeID(String employeeID) {
        Optional<Signature> signatureOptional = signatureRepository.findByEmployeeID(employeeID);
        
        if (signatureOptional.isPresent()) {
            Signature signature = signatureOptional.get();
            
            // Delete the physical file
            try {
                String filePath = signature.getSignature();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            } catch (Exception e) {
                // Log error but continue with database deletion
                System.err.println("Error deleting signature file: " + e.getMessage());
            }
            
            // Delete from database
            signatureRepository.delete(signature);
            return true;
        }
        
        return false;
    }

    // Check if signature exists for employee
    public boolean signatureExistsByEmployeeID(String employeeID) {
        return signatureRepository.existsByEmployeeID(employeeID);
    }

    // Private helper method to save the signature file
    private String saveSignatureFile(String employeeID, MultipartFile signatureFile) throws IOException {
        if (signatureFile.isEmpty()) {
            throw new RuntimeException("Signature file is empty");
        }

        String originalFileName = signatureFile.getOriginalFilename();
        String fileExtension = "";
        
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // Create directory if it doesn't exist
        File directory = new File(DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate unique filename
        String fileName = employeeID + "_signature" + fileExtension;
        File destinationFile = new File(DIRECTORY, fileName);

        // Delete existing file if it exists
        if (destinationFile.exists()) {
            destinationFile.delete();
        }

        // Save the new file
        signatureFile.transferTo(destinationFile);
        
        return destinationFile.getAbsolutePath();
    }

    // Convert entity to DTO
    private SignatureDTO convertToDTO(Signature signature, Employee employee) {
        SignatureDTO dto = new SignatureDTO();
        dto.setId(signature.getId());
        dto.setEmployeeID(signature.getEmployeeID());
        dto.setSignature(signature.getSignature());
        dto.setUploadedAt(signature.getUploadedAt());
        
        // Add employee information
        dto.setEmployeeFirstName(employee.getFirstName());
        dto.setEmployeeLastName(employee.getLastName());
        dto.setEmployeeEmail(employee.getEmail());
        
        return dto;
    }
}
