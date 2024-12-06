package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.IncidentTypeDTO;
import com.skycareSolutions.skyCarePortal.model.IncidentType;
import com.skycareSolutions.skyCarePortal.repository.IncidentTypeRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidentTypeService {

    @Autowired
    private IncidentTypeRepository incidentTypeRepository;

    public List<IncidentTypeDTO> getAllIncidentTypes() {
        return incidentTypeRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public IncidentTypeDTO getIncidentTypeById(String id) {
        IncidentType incidentType = incidentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("IncidentType not found"));
        return convertToDTO(incidentType);
    }

    public IncidentType getIncidentTypeByIdWithEntitiy(String id) {
        IncidentType incidentType = incidentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("IncidentType not found"));
        return incidentType;
    }

    public IncidentTypeDTO createIncidentType(IncidentTypeDTO dto) {
        String lastIncidentTypeID = incidentTypeRepository.getLastIdOfIncidentType();
        String newIncidentTypeID = Micro_utils.idGenerator("incidentType",lastIncidentTypeID);
        dto.setIncidentTypeID(newIncidentTypeID);

        IncidentType incidentType = convertToEntity(dto);
        incidentType = incidentTypeRepository.save(incidentType);
        return convertToDTO(incidentType);
    }

    public IncidentTypeDTO updateIncidentType(String id, IncidentTypeDTO dto) {
        IncidentType existing = incidentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("IncidentType not found"));
        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        IncidentType updated = incidentTypeRepository.save(existing);
        return convertToDTO(updated);
    }

    public void deleteIncidentType(String id) {
        incidentTypeRepository.deleteById(id);
    }

    private IncidentTypeDTO convertToDTO(IncidentType entity) {
        return new IncidentTypeDTO(entity.getIncidentTypeID(), entity.getTitle(), entity.getDescription());
    }

    private IncidentType convertToEntity(IncidentTypeDTO dto) {
        return new IncidentType(dto.getIncidentTypeID(), dto.getTitle(), dto.getDescription());
    }
}
