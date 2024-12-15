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
        dto.setStatus("Active");

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
        IncidentTypeDTO incidentTypeDTO = new IncidentTypeDTO();
        incidentTypeDTO.setDescription(entity.getDescription());
        incidentTypeDTO.setTitle(entity.getTitle());
        incidentTypeDTO.setStatus(entity.getStatus());
        incidentTypeDTO.setIncidentTypeID(entity.getIncidentTypeID());
        return incidentTypeDTO;
    }

    private IncidentType convertToEntity(IncidentTypeDTO dto) {
        IncidentType incidentType = new IncidentType();
        incidentType.setIncidentTypeID(dto.getIncidentTypeID());
        incidentType.setDescription(dto.getDescription());
        incidentType.setStatus(dto.getStatus());
        incidentType.setTitle(dto.getTitle());
        return incidentType;
    }
}
