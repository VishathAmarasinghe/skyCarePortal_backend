package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.IncidentsDTO;
import com.skycareSolutions.skyCarePortal.model.AppointmentAddress;
import com.skycareSolutions.skyCarePortal.model.Incidents;
import com.skycareSolutions.skyCarePortal.repository.IncidentRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    @Lazy
    private CarePlanService carePlanService;

    @Autowired
    @Lazy
    private AppointmentService appointmentService;

    @Autowired
    @Lazy
    private IncidentStatusService incidentStatusService;

    @Autowired
    @Lazy
    private IncidentTypeService incidentTypeService;

    @Autowired
    @Lazy
    private TaskService taskService;

    @Autowired
    @Lazy
    private AppointmentAddressService appointmentAddressService;

    // Get all incidents
    public List<IncidentsDTO> getAllIncidents() {
        return incidentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get an incident by ID
    public IncidentsDTO getIncidentById(String id) {
        Incidents incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with ID: " + id));
        return convertToDTO(incident);
    }

    // Create a new incident
    public IncidentsDTO createIncident(IncidentsDTO incidentsDTO) {
        String lastIncidentID = incidentRepository.getLastIdOfIncident();
        String newIncidentID = Micro_utils.idGenerator("incidents",lastIncidentID);
        incidentsDTO.setIncidentID(newIncidentID);

        AppointmentAddress appointmentAddress =  appointmentAddressService.save(incidentsDTO.getAddress());
        Incidents incident = convertToEntity(incidentsDTO);
        incident.setAppointmentAddress(appointmentAddress);

        return convertToDTO(incidentRepository.save(incident));
    }

    // Update an incident by ID
    public IncidentsDTO updateIncident(String id, IncidentsDTO incidentsDTO) {
        Incidents existingIncident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with ID: " + id));

        existingIncident.setTitle(incidentsDTO.getTitle());
        existingIncident.setReportDate(incidentsDTO.getReportDate());
        existingIncident.setIncidentDate(incidentsDTO.getIncidentDate());
        existingIncident.setIncidentTime(incidentsDTO.getIncidentTime());
        existingIncident.setIssue(incidentsDTO.getIssue());
        existingIncident.setDescription(incidentsDTO.getDescription());
        existingIncident.setHospitalized(incidentsDTO.isHospitalized());
        existingIncident.setAdmitDate(incidentsDTO.getAdmitDate());
        existingIncident.setDischargeDate(incidentsDTO.getDischargeDate());

        // Relationships (assuming they are optional and can be updated using IDs)
        // For a production-grade application, services for relationships should be added.
        existingIncident.getIncidentType().setIncidentTypeID(incidentsDTO.getIncidentTypeID());
        existingIncident.getIncidentStatus().setIncidentStatusID(incidentsDTO.getIncidentStatusID());

        return convertToDTO(incidentRepository.save(existingIncident));
    }

    // Delete an incident by ID
    public void deleteIncident(String id) {
        incidentRepository.deleteById(id);
    }

    public Incidents getIncidentByIDWithEntity(String incidentID){
        Optional<Incidents> incident = incidentRepository.findById(incidentID);
        if (incident.isPresent()){
            return incident.get();
        }else{
            throw new RuntimeException();
        }
    }


    private IncidentsDTO convertToDTO(Incidents incident) {
        IncidentsDTO incidentsDTO = new IncidentsDTO();
        incidentsDTO.setIncidentID(incident.getIncidentID());
        incidentsDTO.setTitle(incident.getTitle());
        incidentsDTO.setReportDate(incident.getReportDate());
        incidentsDTO.setIncidentDate(incident.getIncidentDate());
        incidentsDTO.setIncidentTime(incident.getIncidentTime());
        incidentsDTO.setIncidentTypeID(incident.getIncidentType() != null ? incident.getIncidentType().getIncidentTypeID() : null);
        incidentsDTO.setIncidentStatusID(incident.getIncidentStatus() != null ? incident.getIncidentStatus().getIncidentStatusID() : null);
        incidentsDTO.setIssue(incident.getIssue());
        incidentsDTO.setDescription(incident.getDescription());
        incidentsDTO.setHospitalized(incident.isHospitalized());
        incidentsDTO.setAdmitDate(incident.getAdmitDate());
        incidentsDTO.setDischargeDate(incident.getDischargeDate());
        return incidentsDTO;
    }


    // Convert DTO to Entity
    private Incidents convertToEntity(IncidentsDTO dto) {
        Incidents incident = new Incidents();
        incident.setIncidentID(dto.getIncidentID());
        incident.setTitle(dto.getTitle());
        incident.setReportDate(dto.getReportDate());
        incident.setIncidentDate(dto.getIncidentDate());
        incident.setIncidentTime(dto.getIncidentTime());
        incident.setIssue(dto.getIssue());
        incident.setDescription(dto.getDescription());
        incident.setHospitalized(dto.isHospitalized());
        incident.setFollowUp(dto.getFollowUp());
        incident.setAdmitDate(dto.getAdmitDate());
        incident.setDischargeDate(dto.getDischargeDate());

        if (dto.getCarePlanID()!=null && !dto.getCarePlanID().isEmpty()){
            incident.setCarePlan(carePlanService.findById(dto.getCarePlanID()).orElseThrow());
        }

        if (dto.getAppointmentID()!=null && !dto.getAppointmentID().isEmpty()){
            incident.setAppointment(appointmentService.getAppointmentByIdWithEntity(dto.getAppointmentID()));
        }

        if (dto.getTaskID()!=null && !dto.getTaskID().isEmpty()){
            incident.setTask(taskService.getTaskByIdWithEntity(dto.getTaskID()));
        }

        incident.setIncidentStatus(incidentStatusService.getStatusByIdWithEntity(dto.getIncidentStatusID()));
        incident.setIncidentType(incidentTypeService.getIncidentTypeByIdWithEntitiy(dto.getIncidentTypeID()));

        return incident;
    }
}
