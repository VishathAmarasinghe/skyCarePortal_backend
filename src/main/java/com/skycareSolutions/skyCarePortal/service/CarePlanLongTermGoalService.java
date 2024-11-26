package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.CarePlanLongTermGoalDTO;
import com.skycareSolutions.skyCarePortal.model.CarePlan;
import com.skycareSolutions.skyCarePortal.model.CarePlanLongTermGoal;
import com.skycareSolutions.skyCarePortal.model.GoalOutcome;
import com.skycareSolutions.skyCarePortal.repository.CarePlanLongTermRepository;
import com.skycareSolutions.skyCarePortal.repository.CarePlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarePlanLongTermGoalService {

    @Autowired
    private CarePlanLongTermRepository repository;

    @Autowired
    private CarePlanRepository carePlanRepository;

    // Convert Entity to DTO
    public CarePlanLongTermGoalDTO toDTO(CarePlanLongTermGoal entity) {
        CarePlanLongTermGoalDTO dto = new CarePlanLongTermGoalDTO();
        dto.setLongGoalID(entity.getLongGoalID());
        dto.setLongTermGoal(entity.getLongTermGoal());
        dto.setAchieveWay(entity.getAchieveWay());
        dto.setSupportWay(entity.getSupportWay());
        dto.setNotes(entity.getNotes());
        dto.setCareplanID(entity.getCarePlanID().getCareplanID());

        return dto;
    }

    // Convert DTO to Entity
    public CarePlanLongTermGoal toEntity(CarePlanLongTermGoalDTO dto) {
        CarePlanLongTermGoal entity = new CarePlanLongTermGoal();
        entity.setLongGoalID(dto.getLongGoalID());
        entity.setLongTermGoal(dto.getLongTermGoal());
        entity.setAchieveWay(dto.getAchieveWay());
        entity.setSupportWay(dto.getSupportWay());
        entity.setNotes(dto.getNotes());
        // Assuming carePlanID is fetched and mapped appropriately

        CarePlan carePlan = carePlanRepository.findById(dto.getCareplanID())
                .orElseThrow(() -> new RuntimeException("CarePlan not found with ID: " + dto.getCareplanID()));
        entity.setCarePlanID(carePlan);

        return entity;
    }

    // Save or Update
    public CarePlanLongTermGoalDTO saveOrUpdate(CarePlanLongTermGoalDTO dto) {
        CarePlanLongTermGoal entity = toEntity(dto);
        return toDTO(repository.save(entity));
    }

    // Find by ID
    public Optional<CarePlanLongTermGoalDTO> findById(String id) {
        return repository.findById(id).map(this::toDTO);
    }

    // Get All
    public List<CarePlanLongTermGoalDTO> findAll() {
        return ((List<CarePlanLongTermGoal>) repository.findAll())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Delete by ID
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public List<CarePlanLongTermGoalDTO> findByCarePlan(CarePlan carePlan){
        return ((List<CarePlanLongTermGoal>) repository.findLongTermCarePlansByCarePlanID(carePlan))
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
