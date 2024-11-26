package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.CarePlanStatusDTO;
import com.skycareSolutions.skyCarePortal.model.CarePlanStatus;
import com.skycareSolutions.skyCarePortal.repository.CarePlanStatusRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarePlanStatusService {

    @Autowired
    private CarePlanStatusRepository carePlanStatusRepository;

    // Get all careplan statuses
    public List<CarePlanStatusDTO> getAllCarePlanStatuses() {
        return ((List<CarePlanStatus>) carePlanStatusRepository.findAll())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get a careplan status by ID
    public CarePlanStatusDTO getCarePlanStatusById(String id) {
        CarePlanStatus carePlanStatus = carePlanStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CarePlanStatus not found with ID: " + id));
        return mapToDTO(carePlanStatus);
    }

    // Create a careplan status
    public CarePlanStatusDTO createCarePlanStatus(CarePlanStatusDTO carePlanStatusDTO) {
        String lastCarePlanStatusID = carePlanStatusRepository.getLastIdOfCarePlanStatus();
        String newCarePlanStatusID = Micro_utils.idGenerator("carePlanStatus",lastCarePlanStatusID);
        carePlanStatusDTO.setCareplanStatusID(newCarePlanStatusID);
        CarePlanStatus carePlanStatus = mapToEntity(carePlanStatusDTO);
        carePlanStatus = carePlanStatusRepository.save(carePlanStatus);
        return mapToDTO(carePlanStatus);
    }

    // Update a careplan status
    public CarePlanStatusDTO updateCarePlanStatus(String id, CarePlanStatusDTO carePlanStatusDTO) {
        CarePlanStatus carePlanStatus = carePlanStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CarePlanStatus not found with ID: " + id));

        carePlanStatus.setStatus(carePlanStatusDTO.getStatus());
        carePlanStatus = carePlanStatusRepository.save(carePlanStatus);

        return mapToDTO(carePlanStatus);
    }

    // Delete a careplan status
    public ResponseEntity<Void> deleteCarePlanStatus(String id) {
        if (!carePlanStatusRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        carePlanStatusRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Utility: Map CarePlanStatus to DTO
    private CarePlanStatusDTO mapToDTO(CarePlanStatus carePlanStatus) {
        CarePlanStatusDTO dto = new CarePlanStatusDTO();
        dto.setCareplanStatusID(carePlanStatus.getCareplanStatusID());
        dto.setStatus(carePlanStatus.getStatus());
        return dto;
    }

    // Utility: Map DTO to CarePlanStatus
    private CarePlanStatus mapToEntity(CarePlanStatusDTO dto) {
        CarePlanStatus carePlanStatus = new CarePlanStatus();
        carePlanStatus.setCareplanStatusID(dto.getCareplanStatusID());
        carePlanStatus.setStatus(dto.getStatus());
        return carePlanStatus;
    }
}
