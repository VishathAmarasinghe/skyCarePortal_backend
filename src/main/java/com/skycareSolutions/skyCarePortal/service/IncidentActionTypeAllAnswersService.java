package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.IncidentActionTypeAllAnswersDTO;
import com.skycareSolutions.skyCarePortal.model.IncidentActionTypeAllAnswers;
import com.skycareSolutions.skyCarePortal.model.IncidentActionTypes;
import com.skycareSolutions.skyCarePortal.model.IncidentSubActionTypes;
import com.skycareSolutions.skyCarePortal.model.Incidents;
import com.skycareSolutions.skyCarePortal.repository.IncidentActionTypeAllAnswersRepository;
import com.skycareSolutions.skyCarePortal.repository.IncidentActionTypesRepository;

import com.skycareSolutions.skyCarePortal.repository.IncidentRepository;
import com.skycareSolutions.skyCarePortal.repository.IncidentSubActionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IncidentActionTypeAllAnswersService {

    @Autowired
    private IncidentActionTypeAllAnswersRepository repository;

    @Autowired
    private IncidentActionTypesRepository actionTypesRepository;

    @Autowired
    private IncidentSubActionTypeRepository subActionTypesRepository;

    @Autowired
    private IncidentRepository incidentsRepository;

    // Save a new answer
    public IncidentActionTypeAllAnswersDTO saveAnswer(IncidentActionTypeAllAnswersDTO answerDTO) {
        // Convert DTO to Entity
        IncidentActionTypeAllAnswers entity = toEntity(answerDTO);

        // Save and return as DTO
        return toDTO(repository.save(entity));
    }

    // Get all answers
    public List<IncidentActionTypeAllAnswersDTO> getAllAnswers() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get an answer by ID
    public Optional<IncidentActionTypeAllAnswersDTO> getAnswerById(Long id) {
        return repository.findById(id).map(this::toDTO);
    }

    // Get answers by IncidentActionTypes
    public List<IncidentActionTypeAllAnswersDTO> getAnswersByAction(String incidentActionID) {
        return repository.findByIncidentActionTypes_IncidentActionID(incidentActionID)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get answers by IncidentSubActionTypes
    public List<IncidentActionTypeAllAnswersDTO> getAnswersBySubAction(Long incidentSubActionID) {
        return repository.findByIncidentSubActionTypes_Id(incidentSubActionID)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get answers by Incident ID
    public List<IncidentActionTypeAllAnswersDTO> getAnswersByIncident(String incidentID) {
        Optional<Incidents> incidents = incidentsRepository.findById(incidentID);
        if (incidents.isPresent()){
            return repository.findByIncidentsId(incidents.get())
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }else{
            throw new RuntimeException();
        }
    }

    // Delete an answer by ID
    public void deleteAnswer(Long id) {
        repository.deleteById(id);
    }

    public void deleteAllAnswersRelatedToIncident(String incidentID){
        Optional<Incidents> incidents = incidentsRepository.findById(incidentID);
        if (incidents.isPresent()){
            List<IncidentActionTypeAllAnswers> answerList = repository.findByIncidentsId(incidents.get());
            for (IncidentActionTypeAllAnswers answer:answerList){
                deleteAnswer(answer.getId());
            }
        }
    }

    // Convert Entity to DTO
    private IncidentActionTypeAllAnswersDTO toDTO(IncidentActionTypeAllAnswers entity) {
        IncidentActionTypeAllAnswersDTO dto = new IncidentActionTypeAllAnswersDTO();
        dto.setId(entity.getId());
        dto.setIncidentActionID(entity.getIncidentActionTypes() != null ? entity.getIncidentActionTypes().getIncidentActionID() : null);
        dto.setIncidentSubActionID(entity.getIncidentSubActionTypes() != null ? entity.getIncidentSubActionTypes().getId() : null);
        dto.setIncidentID(entity.getIncidents() != null ? entity.getIncidents().getIncidentID() : null);
        dto.setAnswer(entity.getAnswer());
        return dto;
    }

    // Convert DTO to Entity
    private IncidentActionTypeAllAnswers toEntity(IncidentActionTypeAllAnswersDTO dto) {
        IncidentActionTypeAllAnswers entity = new IncidentActionTypeAllAnswers();
        entity.setId(dto.getId());

        // Fetch relationships from the database and set them
        if (dto.getIncidentActionID() != null) {
            IncidentActionTypes actionType = actionTypesRepository.findById(dto.getIncidentActionID())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid IncidentActionID: " + dto.getIncidentActionID()));
            entity.setIncidentActionTypes(actionType);
        }

        if (dto.getIncidentSubActionID() != null) {
            IncidentSubActionTypes subActionType = subActionTypesRepository.findById(dto.getIncidentSubActionID())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid IncidentSubActionID: " + dto.getIncidentSubActionID()));
            entity.setIncidentSubActionTypes(subActionType);
        }

        if (dto.getIncidentID() != null) {
            Incidents incident = incidentsRepository.findById(dto.getIncidentID())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid IncidentID: " + dto.getIncidentID()));
            entity.setIncidents(incident);
        }

        entity.setAnswer(dto.getAnswer());
        return entity;
    }
}
