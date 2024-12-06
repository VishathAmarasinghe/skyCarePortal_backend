package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.IncidentStatusDTO;
import com.skycareSolutions.skyCarePortal.model.IncidentStatus;
import com.skycareSolutions.skyCarePortal.repository.IncidentStatusRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidentStatusService {

    @Autowired
    private IncidentStatusRepository incidentStatusRepository;

    public List<IncidentStatusDTO> getAllStatuses() {
        return incidentStatusRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public IncidentStatusDTO getStatusById(String id) {
        IncidentStatus status = incidentStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Status not found"));
        return convertToDTO(status);
    }

    public IncidentStatus getStatusByIdWithEntity(String id) {
        IncidentStatus status = incidentStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Status not found"));
        return status;
    }



    public IncidentStatusDTO createStatus(IncidentStatusDTO dto) {

        String lastIncidentStatusID = incidentStatusRepository.getLastIdOfIncidentStatus();
        String newIncidentStatusID = Micro_utils.idGenerator("incidentStatus",lastIncidentStatusID);
        dto.setIncidentStatusID(newIncidentStatusID);

        IncidentStatus status = convertToEntity(dto);
        return convertToDTO(incidentStatusRepository.save(status));
    }

    public IncidentStatusDTO updateStatus(String id, IncidentStatusDTO dto) {
        IncidentStatus status = incidentStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Status not found"));
        status.setStatus(dto.getStatus());
        status.setDescription(dto.getDescription());
        status.setActiveState(dto.getActiveState());
        return convertToDTO(incidentStatusRepository.save(status));
    }

    public void deleteStatus(String id) {
        incidentStatusRepository.deleteById(id);
    }

    private IncidentStatusDTO convertToDTO(IncidentStatus entity) {
        return new IncidentStatusDTO(entity.getIncidentStatusID(), entity.getStatus(), entity.getDescription(), entity.getActiveState());
    }

    private IncidentStatus convertToEntity(IncidentStatusDTO dto) {
        return new IncidentStatus(dto.getIncidentStatusID(), dto.getStatus(), dto.getDescription(), dto.getActiveState());
    }
}
