package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.CreateIncidentQuestionsDTO;
import com.skycareSolutions.skyCarePortal.dto.IncidentActionTypesDTO;
import com.skycareSolutions.skyCarePortal.dto.IncidentSubActionTypesDTO;
import com.skycareSolutions.skyCarePortal.model.IncidentActionTypes;
import com.skycareSolutions.skyCarePortal.model.IncidentSubActionTypes;
import com.skycareSolutions.skyCarePortal.repository.IncidentActionTypesRepository;
import com.skycareSolutions.skyCarePortal.repository.IncidentSubActionTypeRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidentActionTypesService {

    @Autowired
    private IncidentActionTypesRepository incidentActionTypesRepository;

    @Autowired
    private IncidentSubActionTypeRepository incidentSubActionTypeRepository;

    public List<IncidentActionTypesDTO> getAllActionTypes() {
        return incidentActionTypesRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public IncidentActionTypesDTO getActionTypeById(String id) {
        IncidentActionTypes actionType = incidentActionTypesRepository.findById(id).orElseThrow(() -> new RuntimeException("Action Type not found"));
        return convertToDTO(actionType);
    }

    public Boolean createActionTypeWithSubQuestions(List<CreateIncidentQuestionsDTO> incidentQuestionsList){
        for (CreateIncidentQuestionsDTO item:incidentQuestionsList){
            IncidentActionTypesDTO incidentActionTypesDTO = new IncidentActionTypesDTO();
            incidentActionTypesDTO.setQuestion(item.getMainQuestion());
            incidentActionTypesDTO.setStatus("Active");
            incidentActionTypesDTO.setYesNoAnswer(item.isYesNoType());

            incidentActionTypesDTO = createActionType(incidentActionTypesDTO);

            for (String subQuestion:item.getSubQuestions()){
                IncidentSubActionTypesDTO incidentSubActionTypesDTO = new IncidentSubActionTypesDTO();
                incidentSubActionTypesDTO.setQuestion(subQuestion);
                incidentSubActionTypesDTO.setIncidentActionTypesID(incidentActionTypesDTO.getIncidentActionID());
                incidentSubActionTypesDTO.setState("Active");

                createSubActionType(incidentActionTypesDTO.getIncidentActionID(),incidentSubActionTypesDTO);
            }

        }
        return true;

    }

    public IncidentActionTypesDTO createActionType(IncidentActionTypesDTO dto) {
        String lastIncidentActionTypeID = incidentActionTypesRepository.getLastIdOfIncidentActionTypes();
        String newIncidentActionTypeID = Micro_utils.idGenerator("incidentActionType", lastIncidentActionTypeID);
        dto.setIncidentActionID(newIncidentActionTypeID);

        IncidentActionTypes entity = convertToEntity(dto);
        IncidentActionTypes savedEntity = incidentActionTypesRepository.save(entity);
        return convertToDTO(savedEntity);
    }

    public IncidentActionTypesDTO updateActionType(String id, IncidentActionTypesDTO dto) {
        IncidentActionTypes existingEntity = incidentActionTypesRepository.findById(id).orElseThrow(() -> new RuntimeException("Action Type not found"));
        existingEntity.setQuestion(dto.getQuestion());
        existingEntity.setYesNoType(dto.isYesNoAnswer());
        existingEntity.setStatus(dto.getStatus());
        IncidentActionTypes updatedEntity = incidentActionTypesRepository.save(existingEntity);
        return convertToDTO(updatedEntity);
    }

    public void deleteActionType(String id) {
        incidentActionTypesRepository.deleteById(id);
    }

    // SubAction CRUD
    public IncidentSubActionTypesDTO createSubActionType(String actionTypeId, IncidentSubActionTypesDTO subDto) {
        IncidentActionTypes parent = incidentActionTypesRepository.findById(actionTypeId).orElseThrow(() -> new RuntimeException("Action Type not found"));

        IncidentSubActionTypes subEntity = new IncidentSubActionTypes();
        subEntity.setQuestion(subDto.getQuestion());
        subEntity.setState(subDto.getState());
        subEntity.setIncidentActionTypes(parent);

        IncidentSubActionTypes savedEntity = incidentSubActionTypeRepository.save(subEntity);
        return convertSubToDTO(savedEntity);
    }

    public List<IncidentSubActionTypesDTO> getAllSubActionTypes(String actionTypeId) {
        List<IncidentSubActionTypes> subActionList = incidentSubActionTypeRepository.findAllByIncidentActionID(actionTypeId);
         return subActionList.stream().map(this::convertSubToDTO).toList();

    }

    public IncidentSubActionTypesDTO updateSubActionType(Long subActionId, IncidentSubActionTypesDTO subDto) {
        IncidentSubActionTypes existingSub = incidentSubActionTypeRepository.findById(subActionId).orElseThrow(() -> new RuntimeException("Sub Action Type not found"));
        existingSub.setQuestion(subDto.getQuestion());
        existingSub.setState(subDto.getState());

        IncidentSubActionTypes updatedSub = incidentSubActionTypeRepository.save(existingSub);
        return convertSubToDTO(updatedSub);
    }

    public void deleteSubActionType(Long subActionId) {
        incidentSubActionTypeRepository.deleteById(subActionId);
    }

    // Conversion Methods
    private IncidentActionTypesDTO convertToDTO(IncidentActionTypes entity) {
       List<IncidentSubActionTypes> subActionList = incidentSubActionTypeRepository.findAllByIncidentActionID(entity.getIncidentActionID());

       List<IncidentSubActionTypesDTO> subActionTypesDTOList = subActionList.stream().map(this::convertSubToDTO).toList();
       IncidentActionTypesDTO incidentActionTypesDTO = new IncidentActionTypesDTO();
       incidentActionTypesDTO.setIncidentActionID(entity.getIncidentActionID());
       incidentActionTypesDTO.setIncidentSubActionList(subActionTypesDTOList);
       incidentActionTypesDTO.setQuestion(entity.getQuestion());
       incidentActionTypesDTO.setYesNoAnswer(entity.isYesNoType());
       incidentActionTypesDTO.setStatus(entity.getStatus());

       return incidentActionTypesDTO;
    }

    private IncidentActionTypes convertToEntity(IncidentActionTypesDTO dto) {
        IncidentActionTypes entity = new IncidentActionTypes();
        entity.setIncidentActionID(dto.getIncidentActionID());
        entity.setQuestion(dto.getQuestion());
        entity.setYesNoType(dto.isYesNoAnswer());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    private IncidentSubActionTypesDTO convertSubToDTO(IncidentSubActionTypes subEntity) {
        IncidentSubActionTypesDTO incidentSubActionTypesDTO = new IncidentSubActionTypesDTO();
        incidentSubActionTypesDTO.setIncidentActionTypesID(subEntity.getIncidentActionTypes().getIncidentActionID());
        incidentSubActionTypesDTO.setQuestion(subEntity.getQuestion());
        incidentSubActionTypesDTO.setId(subEntity.getId());
        incidentSubActionTypesDTO.setState(subEntity.getState());
        return incidentSubActionTypesDTO;
    }
}
