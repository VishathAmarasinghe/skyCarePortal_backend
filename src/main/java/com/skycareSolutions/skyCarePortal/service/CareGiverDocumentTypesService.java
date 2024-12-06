package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.CareGiverDocumentTypesDTO;
import com.skycareSolutions.skyCarePortal.model.CareGiverDocumentTypes;
import com.skycareSolutions.skyCarePortal.repository.CareGiverDocumentTypesRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CareGiverDocumentTypesService {

    @Autowired
    private CareGiverDocumentTypesRepository repository;

    public List<CareGiverDocumentTypesDTO> getAllDocumentTypes() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CareGiverDocumentTypesDTO> getDocumentTypeById(String id) {
        return repository.findById(id).map(this::mapToDTO);
    }

    public CareGiverDocumentTypesDTO createDocumentType(CareGiverDocumentTypesDTO dto) {
        String lastDocumentTypeID = repository.getLastIdOfdocumentType();
        String newDocumentTypeID = Micro_utils.idGenerator("careGiverdocumentType",lastDocumentTypeID);
        dto.setDocumentTypeID(newDocumentTypeID);
        CareGiverDocumentTypes entity = mapToEntity(dto);
        CareGiverDocumentTypes savedEntity = repository.save(entity);
        return mapToDTO(savedEntity);
    }

    public CareGiverDocumentTypesDTO updateDocumentType(String id, CareGiverDocumentTypesDTO dto) {
        if (repository.existsById(id)) {
            CareGiverDocumentTypes entity = mapToEntity(dto);
            CareGiverDocumentTypes updatedEntity = repository.save(entity);
            return mapToDTO(updatedEntity);
        }
        return null; // Handle not found scenario appropriately.
    }

    public void deleteDocumentType(String id) {
        repository.deleteById(id);
    }

    private CareGiverDocumentTypesDTO mapToDTO(CareGiverDocumentTypes entity) {
        CareGiverDocumentTypesDTO dto = new CareGiverDocumentTypesDTO();
        dto.setDocumentTypeID(entity.getDocumentTypeID());
        dto.setDocumentName(entity.getDocumentName());
        dto.setExpDateNeeded(entity.isExpDateNeeded());
        return dto;
    }

    private CareGiverDocumentTypes mapToEntity(CareGiverDocumentTypesDTO dto) {
        CareGiverDocumentTypes entity = new CareGiverDocumentTypes();
        entity.setDocumentTypeID(dto.getDocumentTypeID());
        entity.setDocumentName(dto.getDocumentName());
        entity.setExpDateNeeded(dto.getExpDateNeeded());
        return entity;
    }
}
