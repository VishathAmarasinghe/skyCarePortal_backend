package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.CareGiverPaymentTypesDTO;
import com.skycareSolutions.skyCarePortal.model.CareGiverPaymentTypes;
import com.skycareSolutions.skyCarePortal.repository.CareGiverPaymentTypesRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CareGiverPaymentTypesService {

    @Autowired
    private CareGiverPaymentTypesRepository repository;

    public List<CareGiverPaymentTypesDTO> getAllPaymentTypes() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CareGiverPaymentTypesDTO> getPaymentTypeById(String id) {
        return repository.findById(id).map(this::mapToDTO);
    }

    public CareGiverPaymentTypesDTO createPaymentType(CareGiverPaymentTypesDTO dto) {
        String lastPaymentTypeID = repository.getLastIdOfPaymentType();
        String newPaymentTypeID = Micro_utils.idGenerator("careGiverPaymentType",lastPaymentTypeID);
        dto.setPaymentTypeID(newPaymentTypeID);
        CareGiverPaymentTypes entity = mapToEntity(dto);
        CareGiverPaymentTypes savedEntity = repository.save(entity);
        return mapToDTO(savedEntity);
    }

    public CareGiverPaymentTypesDTO updatePaymentType(String id, CareGiverPaymentTypesDTO dto) {
        if (repository.existsById(id)) {
            CareGiverPaymentTypes entity = mapToEntity(dto);
            CareGiverPaymentTypes updatedEntity = repository.save(entity);
            return mapToDTO(updatedEntity);
        }
        return null; // Handle not found scenario appropriately.
    }

    public void deletePaymentType(String id) {
        repository.deleteById(id);
    }

    private CareGiverPaymentTypesDTO mapToDTO(CareGiverPaymentTypes entity) {
        CareGiverPaymentTypesDTO dto = new CareGiverPaymentTypesDTO();
        dto.setPaymentTypeID(entity.getPaymentTypeID());
        dto.setPaymentName(entity.getPaymentName());
        dto.setState(entity.getState());
        return dto;
    }

    private CareGiverPaymentTypes mapToEntity(CareGiverPaymentTypesDTO dto) {
        CareGiverPaymentTypes entity = new CareGiverPaymentTypes();
        entity.setPaymentTypeID(dto.getPaymentTypeID());
        entity.setPaymentName(dto.getPaymentName());
        entity.setState(dto.getState());
        return entity;
    }
}
