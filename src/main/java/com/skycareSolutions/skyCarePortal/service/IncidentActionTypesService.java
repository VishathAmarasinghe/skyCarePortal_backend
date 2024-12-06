package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.IncidentActionTypesDTO;
import com.skycareSolutions.skyCarePortal.model.IncidentActionTypes;
import com.skycareSolutions.skyCarePortal.repository.IncidentActionTypesRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidentActionTypesService {

    @Autowired
    private IncidentActionTypesRepository incidentActionTypesRepository;

    public List<IncidentActionTypesDTO> getAllActionTypes() {
        return incidentActionTypesRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public IncidentActionTypesDTO createActionType(IncidentActionTypesDTO dto) {
        String lastIncidentActionTypeID = incidentActionTypesRepository.getLastIdOfIncidentActionTypes();
        String newIncidentActionTypeID = Micro_utils.idGenerator("incidentActionType",lastIncidentActionTypeID);

        dto.setIncidentActionID(newIncidentActionTypeID);

        IncidentActionTypes entity = convertToEntity(dto);
        return convertToDTO(incidentActionTypesRepository.save(entity));
    }

    public void deleteActionType(String id) {
        incidentActionTypesRepository.deleteById(id);
    }

    private IncidentActionTypesDTO convertToDTO(IncidentActionTypes entity) {
        return new IncidentActionTypesDTO(entity.getIncidentActionID(), entity.getQuestion(), entity.isAnswer());
    }

    private IncidentActionTypes convertToEntity(IncidentActionTypesDTO dto) {
        return new IncidentActionTypes(dto.getIncidentActionID(), dto.getQuestion(), dto.isAnswer());
    }
}
