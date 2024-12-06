package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.CarePlanLongTermGoalDTO;
import com.skycareSolutions.skyCarePortal.dto.CarePlanShortTermGoalDTO;
import com.skycareSolutions.skyCarePortal.model.CarePlan;
import com.skycareSolutions.skyCarePortal.model.CarePlanLongTermGoal;
import com.skycareSolutions.skyCarePortal.model.CarePlanShortTermGoal;
import com.skycareSolutions.skyCarePortal.model.GoalOutcome;
import com.skycareSolutions.skyCarePortal.repository.CarePlanLongTermRepository;
import com.skycareSolutions.skyCarePortal.repository.CarePlanRepository;
import com.skycareSolutions.skyCarePortal.repository.CarePlanShortTermRepository;
import com.skycareSolutions.skyCarePortal.repository.GoalOutcomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarePlanShortTermGoalService {

    @Autowired
    private CarePlanShortTermRepository repository;

    @Autowired
    private CarePlanRepository carePlanRepository;

    @Autowired
    private GoalOutcomeRepository goalOutcomeRepository;

    @Autowired
    private CarePlanLongTermRepository carePlanLongTermRepository;

    // Convert Entity to DTO
    public CarePlanShortTermGoalDTO toDTO(CarePlanShortTermGoal entity) {
        CarePlanShortTermGoalDTO dto = new CarePlanShortTermGoalDTO();
        dto.setShortTermGoalID(entity.getShortTermGoalID());
        dto.setLongTermGoalID(entity.getLongTermGoalID().getLongGoalID());
        dto.setGoalTitle(entity.getGoalTitle());
        dto.setGoalDescription(entity.getGoalDescription());
        dto.setGoalOutcomeTypeID(entity.getGoalOutcome().getGoalOutcomeID());
        dto.setOutcomeDetails(entity.getOutcomeDetails());
        dto.setGoalStrategy(entity.getGoalStrategy());
        dto.setCareplanID(entity.getCarePlanID().getCareplanID());
        return dto;
    }

    // Convert DTO to Entity
    public CarePlanShortTermGoal toEntity(CarePlanShortTermGoalDTO dto) {
        CarePlanShortTermGoal entity = new CarePlanShortTermGoal();
        entity.setShortTermGoalID(dto.getShortTermGoalID());
        CarePlanLongTermGoal carePlanLongGoal = carePlanLongTermRepository.findById(dto.getLongTermGoalID())
                .orElseThrow(() -> new RuntimeException("CarePlan not found with ID: " + dto.getLongTermGoalID()));
        entity.setLongTermGoalID(carePlanLongGoal);
        entity.setGoalTitle(dto.getGoalTitle());
        entity.setGoalDescription(dto.getGoalDescription());
        entity.setOutcomeDetails(dto.getOutcomeDetails());
        entity.setGoalStrategy(dto.getGoalStrategy());
        CarePlan carePlan = carePlanRepository.findById(dto.getCareplanID())
                .orElseThrow(() -> new RuntimeException("CarePlan not found with ID: " + dto.getCareplanID()));
        entity.setCarePlanID(carePlan);

        GoalOutcome goalOutcome = goalOutcomeRepository.findById(dto.getGoalOutcomeTypeID())
                .orElseThrow(() -> new RuntimeException("Goal outcome not found with ID: " + dto.getGoalOutcomeTypeID()));
        entity.setGoalOutcome(goalOutcome);
        return entity;
    }

    // Save or Update
    public CarePlanShortTermGoalDTO saveOrUpdate(CarePlanShortTermGoalDTO dto) {

        CarePlanShortTermGoal entity = toEntity(dto);

        return toDTO(repository.save(entity));
    }

    // Find by ID
    public Optional<CarePlanShortTermGoalDTO> findById(String id) {
        return repository.findById(id).map(this::toDTO);
    }

    // Get All
    public List<CarePlanShortTermGoalDTO> findAll() {
        return ((List<CarePlanShortTermGoal>) repository.findAll())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Delete by ID
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    //find by longTerm id
    public List<CarePlanShortTermGoalDTO> findByLongTermGoal(String lognTermGoalID){
        CarePlanLongTermGoal carePlanLongTermGoal = carePlanLongTermRepository.findById(lognTermGoalID)
                .orElseThrow(() -> new RuntimeException("Long Term CarePlan not found with ID: " + lognTermGoalID));
        return (repository.findbyLongTermGoalID(carePlanLongTermGoal))
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
