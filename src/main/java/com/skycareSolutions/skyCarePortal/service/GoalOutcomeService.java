package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.GoalOutcomeDTO;
import com.skycareSolutions.skyCarePortal.model.GoalOutcome;
import com.skycareSolutions.skyCarePortal.repository.GoalOutcomeRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalOutcomeService {

    @Autowired
    private GoalOutcomeRepository repository;

    // Convert DTO to Entity
    private GoalOutcome toEntity(GoalOutcomeDTO dto) {
        GoalOutcome entity = new GoalOutcome();
        entity.setGoalOutcomeID(dto.getGoalOutcomeID());
        entity.setStatus(dto.getStatus());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    // Convert Entity to DTO
    private GoalOutcomeDTO toDTO(GoalOutcome entity) {
        GoalOutcomeDTO dto = new GoalOutcomeDTO();
        dto.setGoalOutcomeID(entity.getGoalOutcomeID());
        dto.setStatus(entity.getStatus());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    // Create or Update GoalOutcome
    public GoalOutcomeDTO saveOrUpdate(GoalOutcomeDTO dto) {
        GoalOutcome entity = toEntity(dto);
        if (dto.getGoalOutcomeID() == null) {
            String lastId = repository.getLastIdOfGoalOutcome();
            String newId = Micro_utils.idGenerator("goalOutcome",lastId);
            entity.setGoalOutcomeID(newId);
        }
        return toDTO(repository.save(entity));
    }

    // Get GoalOutcome by ID
    public GoalOutcomeDTO findById(String id) {
        GoalOutcome entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("GoalOutcome not found with ID: " + id));
        return toDTO(entity);
    }

    // Get All GoalOutcomes
    public List<GoalOutcomeDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Delete GoalOutcome by ID
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
