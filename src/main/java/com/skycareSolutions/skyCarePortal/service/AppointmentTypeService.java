package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.AppointmentTypeDTO;
import com.skycareSolutions.skyCarePortal.model.AppointmentType;
import com.skycareSolutions.skyCarePortal.repository.AppointmentTypesRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentTypeService {

    @Autowired
    private AppointmentTypesRepository appointmentTypesRepository;

    public List<AppointmentTypeDTO> getAllAppointmentTypes() {
        return appointmentTypesRepository.findAllAppointmentTypes()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AppointmentTypeDTO getAppointmentTypeById(String id) {
        AppointmentType appointmentType = appointmentTypesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AppointmentType not found"));
        return mapToDTO(appointmentType);
    }

    public AppointmentType getAppointmentTypeByIdWithEntity(String id) {
        AppointmentType appointmentType = appointmentTypesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AppointmentType not found"));
        return appointmentType;
    }

    public AppointmentTypeDTO createAppointmentType(AppointmentTypeDTO appointmentTypeDTO) {
        String lastAppointmentTypeID = appointmentTypesRepository.getLastIdOfAppointmentType();
        String newAppointmentTypeID = Micro_utils.idGenerator("appointment",lastAppointmentTypeID);
        appointmentTypeDTO.setAppointmentTypeID(newAppointmentTypeID);
        AppointmentType appointmentType = mapToEntity(appointmentTypeDTO);
        appointmentType = appointmentTypesRepository.save(appointmentType);
        return mapToDTO(appointmentType);
    }

    public AppointmentTypeDTO updateAppointmentType(String id, AppointmentTypeDTO appointmentTypeDTO) {
        AppointmentType appointmentType = appointmentTypesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AppointmentType not found"));
        appointmentType.setName(appointmentTypeDTO.getName());
        appointmentType.setStatus(appointmentTypeDTO.getStatus());
        appointmentType.setColor(appointmentTypeDTO.getColor());
        appointmentType = appointmentTypesRepository.save(appointmentType);
        return mapToDTO(appointmentType);
    }

    public void deleteAppointmentType(String id) {
        appointmentTypesRepository.deleteById(id);
    }

    private AppointmentTypeDTO mapToDTO(AppointmentType appointmentType) {
        AppointmentTypeDTO dto = new AppointmentTypeDTO();
        dto.setAppointmentTypeID(appointmentType.getAppointmentTypeID());
        dto.setName(appointmentType.getName());
        dto.setStatus(appointmentType.getStatus());
        dto.setColor(appointmentType.getColor());
        return dto;
    }

    private AppointmentType mapToEntity(AppointmentTypeDTO dto) {
        AppointmentType entity = new AppointmentType();
        entity.setAppointmentTypeID(dto.getAppointmentTypeID());
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        entity.setColor(dto.getColor());
        return entity;
    }
}
