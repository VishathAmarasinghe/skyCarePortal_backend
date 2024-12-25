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

import java.util.ArrayList;
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

    public IncidentActionTypesDTO updateSingleMainQuestionWithSubQuestions(IncidentActionTypesDTO dto){
        IncidentActionTypes exisitingEntity = incidentActionTypesRepository.findById(dto.getIncidentActionID()).orElse(null);
        if (exisitingEntity!=null){
            exisitingEntity.setQuestion(dto.getQuestion());
            exisitingEntity.setStatus(dto.getStatus());
            exisitingEntity.setYesNoType(dto.isYesNoAnswer());
            incidentActionTypesRepository.save(exisitingEntity);
        }


        List<IncidentSubActionTypes> existingSubTypes = incidentSubActionTypeRepository.findAllByIncidentActionID(dto.getIncidentActionID());
        List<IncidentSubActionTypesDTO> incomingSubTypes = dto.getIncidentSubActionList();

        // Step 3: Update existing sub-questions and collect new sub-questions to add
        List<IncidentSubActionTypesDTO> updatedSubTypes = new ArrayList<>();
        List<IncidentSubActionTypesDTO> toAdd = new ArrayList<>();

        // Map existing sub-action types by their ID for easy comparison
        List<IncidentSubActionTypesDTO> existingSubDTOs = existingSubTypes.stream()
                .map(this::convertSubToDTO)
                .collect(Collectors.toList());

        for (IncidentSubActionTypesDTO incomingSub : incomingSubTypes) {
            boolean subExists = false;

            // Check if the sub-action already exists
            for (IncidentSubActionTypesDTO existingSub : existingSubDTOs) {
                if (incomingSub.getId().equals(existingSub.getId())) {
                    // Update existing sub-action
                    IncidentSubActionTypes existingSubEntity = incidentSubActionTypeRepository.findById(existingSub.getId())
                            .orElseThrow(() -> new RuntimeException("Sub Action Type not found"));

                    existingSubEntity.setQuestion(incomingSub.getQuestion());
                    existingSubEntity.setState(incomingSub.getState());

                    incidentSubActionTypeRepository.save(existingSubEntity); // Save the updated sub-action
                    updatedSubTypes.add(convertSubToDTO(existingSubEntity));
                    subExists = true;
                    break;
                }
            }

            // If the sub-action doesn't exist, add it
            if (!subExists) {
                IncidentSubActionTypesDTO newSubDTO = new IncidentSubActionTypesDTO();
                newSubDTO.setQuestion(incomingSub.getQuestion());
                newSubDTO.setState(incomingSub.getState());
                newSubDTO.setIncidentActionTypesID(dto.getIncidentActionID());

                // Create a new sub-action type
                IncidentSubActionTypes newSubEntity = new IncidentSubActionTypes();
                newSubEntity.setQuestion(newSubDTO.getQuestion());
                newSubEntity.setState(newSubDTO.getState());
                newSubEntity.setIncidentActionTypes(exisitingEntity); // Set the parent action type

                IncidentSubActionTypes savedSubEntity = incidentSubActionTypeRepository.save(newSubEntity); // Save new sub-action
                toAdd.add(convertSubToDTO(savedSubEntity)); // Add it to the list of newly created sub-actions
            }
        }

        // Step 4: Return the DTO of the main action type and its updated sub-action types
        IncidentActionTypesDTO updatedActionTypeDTO = convertToDTO(exisitingEntity);
        updatedActionTypeDTO.setIncidentSubActionList(updatedSubTypes); // Add updated sub-actions to the main action DTO

        // Optionally, you can also include newly added sub-actions
        updatedActionTypeDTO.getIncidentSubActionList().addAll(toAdd);

        return updatedActionTypeDTO;
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
