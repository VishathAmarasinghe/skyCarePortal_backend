package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.ClassificationDTO;
import com.skycareSolutions.skyCarePortal.model.Classification;
import com.skycareSolutions.skyCarePortal.repository.ClassificationRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClassificationService {

    @Autowired
    private ClassificationRepository classificationRepository;

    // Create a new classification
    public void createClassification(ClassificationDTO classificationDTO) throws Exception {
        // Check for duplicates by classification name
        if (classificationRepository.existsByClassificationName(classificationDTO.getClassificationName())) {
            throw new Exception("Classification with the same name already exists.");
        }
        String lastClassificationID = classificationRepository.getLastIdOfClassification();
        String newClassificationID = Micro_utils.idGenerator("classification",lastClassificationID);
        classificationDTO.setClassificationID(newClassificationID);
        Classification classification = new Classification();
        classification.setClassificationID(classificationDTO.getClassificationID());
        classification.setClassificationName(classificationDTO.getClassificationName());
        classification.setState(classificationDTO.getState());

        classificationRepository.save(classification);
    }

    // Get all classifications
    public List<ClassificationDTO> getAllClassifications() {
        return classificationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get a classification by name
    public ClassificationDTO getClassificationByName(String name) {
        Classification classification = classificationRepository.findByClassificationName(name);
        return classification != null ? convertToDTO(classification) : null;
    }

    // Get a classification by id
    public ClassificationDTO getClassificationByID(String id) {
        return classificationRepository.findById(id)
                .map(this::convertToDTO) // Convert to DTO if present
                .orElse(null); // Return null if not present
    }

    // Update an existing classification
    public void updateClassification(String id, ClassificationDTO classificationDTO) throws Exception {
        Classification existingClassification = classificationRepository.findById(id).orElse(null);
        if (existingClassification == null) {
            throw new Exception("Classification not found");
        }

        Classification classification = classificationRepository.findByClassificationName(classificationDTO.getClassificationName());

        if (classification!=null){
        // Prevent duplicate classification name when updating
        if (!classification.getClassificationID().equalsIgnoreCase(id)) {
            throw new Exception("A classification with this name already exists.");
        }
        }

        existingClassification.setClassificationName(classificationDTO.getClassificationName());
        existingClassification.setState(classificationDTO.getState());
        classificationRepository.save(existingClassification);
    }

    // Delete a classification by ID
    public void deleteClassification(String id) {
        classificationRepository.deleteById(id);
    }

    // Helper method to convert Classification model to DTO
    private ClassificationDTO convertToDTO(Classification classification) {
        ClassificationDTO classificationDTO = new ClassificationDTO();
        classificationDTO.setClassificationID(classification.getClassificationID());
        classificationDTO.setClassificationName(classification.getClassificationName());
        classificationDTO.setState(classification.getState());
        return classificationDTO;
    }
}
