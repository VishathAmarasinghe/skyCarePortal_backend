package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.IncidentRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import com.skycareSolutions.skyCarePortal.utils.TemporaryHiddenClients;
import com.skycareSolutions.skyCarePortal.utils.TemporaryHiddenEmployees;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IncidentService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "incidents").toString();

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
    private IncidentActionTypeAllAnswersService incidentActionTypeAllAnswersService;

    @Autowired
    @Lazy
    private AppointmentAddressService appointmentAddressService;

    @Autowired
    @Lazy
    private IncidentInvolvedPartiesService incidentInvolvedPartiesService;

    @Autowired
    @Lazy
    private IncidentDocumentService incidentDocumentServicel;

    @Autowired
    @Lazy
    private EmployeeService employeeService;

    @Autowired
    @Lazy
    private ClientService clientService;

    // Get all incidents
    public List<IncidentsDTO> getAllIncidents() {
        return incidentRepository.findAll().stream()
                .filter(this::isVisibleIncident)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<IncidentsDTO> getAllIncidentsByEmployeeID(String employeeID){
        if (TemporaryHiddenEmployees.isHiddenEmployee(employeeID)) {
            return List.of();
        }
        Employee employee = employeeService.findOneEmployeeWithEntity(employeeID);
        List<Incidents> incidents = incidentRepository.getIncidentsRelatedToEmployee(employee);
        List<IncidentsDTO> dtoList = new ArrayList<>();
        for (Incidents inc:incidents){
            if (!isVisibleIncident(inc)) {
                continue;
            }
            dtoList.add(convertToDTO(inc));
        }
        return dtoList;
    }

    // Get an incident by ID
    public IncidentsDTO getIncidentById(String id) {
        Incidents incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with ID: " + id));
        if (!isVisibleIncident(incident)) {
            throw new RuntimeException("Incident not found with ID: " + id);
        }
        IncidentsDTO incidentValues = convertToDTO(incident);
        incidentValues.setParties(incidentInvolvedPartiesService.getPartiesByIncident(id));
        incidentValues.setDocuments(incidentDocumentServicel.getDocumentsRelatedToIncident(id));
        incidentValues.setAnswers(incidentActionTypeAllAnswersService.getAnswersByIncident(id));
        return incidentValues;
    }

    private boolean isVisibleIncident(Incidents incident) {
        if (incident == null) {
            return false;
        }
        if (incident.getClient() != null && TemporaryHiddenClients.isHidden(incident.getClient().getClientID())) {
            return false;
        }
        if (incident.getEmployee() != null
                && TemporaryHiddenEmployees.isHiddenEmployee(incident.getEmployee().getEmployeeID())) {
            return false;
        }
        return true;
    }

    // Create a new incident
    public IncidentsDTO createIncident(IncidentsDTO incidentsDTO, List<MultipartFile> files) {
        // Check for duplicate incident
        if (incidentsDTO.getClientID() != null && !incidentsDTO.getClientID().isEmpty()) {
            Client client = clientService.getClientByIdWithEntitiy(incidentsDTO.getClientID());
            Employee employee = employeeService.findOneEmployeeWithEntity(incidentsDTO.getEmployeeID());
            
            Optional<Incidents> duplicateIncident = incidentRepository.findDuplicateIncident(
                    incidentsDTO.getIncidentDate(),
                    incidentsDTO.getIncidentTime(),
                    client,
                    employee
            );
            
            if (duplicateIncident.isPresent()) {
                throw new RuntimeException("A duplicate incident already exists with the same date, time, client, and employee.");
            }
        }
        
        String lastIncidentID = incidentRepository.getLastIdOfIncident();
        String newIncidentID = Micro_utils.idGenerator("incidents",lastIncidentID);
        incidentsDTO.setIncidentID(newIncidentID);
        Incidents incident = convertToEntity(incidentsDTO);

        if (incidentsDTO.getAddress()!=null){
            AppointmentAddress appointmentAddress =  appointmentAddressService.save(incidentsDTO.getAddress());
            incident.setAppointmentAddress(appointmentAddress);
        }


        Incidents incident1 = incidentRepository.save(incident);

        for(IncidentInvolvedPartiesDTO party:incidentsDTO.getParties()){
            party.setIncidentID(incident1.getIncidentID());
            incidentInvolvedPartiesService.createParty(party);
        }
        for (IncidentActionTypeAllAnswersDTO answer:incidentsDTO.getAnswers()){
            answer.setIncidentID(incident1.getIncidentID());
            incidentActionTypeAllAnswersService.saveAnswer(answer);
        }
        try {
            if (files!=null && !files.isEmpty()){
                for (MultipartFile file:files){
                    if (file!=null){
                        String originalFileName = file.getOriginalFilename();
                        String fileExtension = originalFileName;
                        if (originalFileName != null && originalFileName.contains(".")) {
                            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                        }
                        System.out.println("original file name "+originalFileName);
                        File directory = new File(DIRECTORY);
                        if(!directory.exists()){
                            directory.mkdirs();
                        }
                        File destinationFile = new File(DIRECTORY, incident1.getIncidentID()+"_"+originalFileName+ fileExtension);
                        if (destinationFile.exists()) {
                            // Delete the existing file
                            boolean deleted = destinationFile.delete();
                            if (!deleted) {
                                throw new RuntimeException("Failed to delete the existing file: " + destinationFile.getName());
                            }
                        }

                        file.transferTo(destinationFile);
                        IncidentDocumentsDTO incidentDocumentsDTO = new IncidentDocumentsDTO();
                        incidentDocumentsDTO.setIncidentID(incident1.getIncidentID());
                        incidentDocumentsDTO.setDocumentLocation(destinationFile.getAbsolutePath());
                        incidentDocumentsDTO.setDocumentName(originalFileName);

                        incidentDocumentServicel.saveDocument(incidentDocumentsDTO);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return convertToDTO(incident1);
    }

    // Update an incident by ID
    public IncidentsDTO updateIncident(String id, IncidentsDTO incidentsDTO, List<MultipartFile> files) {
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
        existingIncident.setFollowUp(incidentsDTO.getFollowUp());
        existingIncident.setNotes(incidentsDTO.getNotes());

        if (incidentsDTO.getClientID() != null && !incidentsDTO.getClientID().isBlank()) {
            existingIncident.setClient(clientService.getClientByIdWithEntitiy(incidentsDTO.getClientID()));
        } else {
            existingIncident.setClient(null);
        }

        if (incidentsDTO.getCarePlanID() != null && !incidentsDTO.getCarePlanID().isBlank()) {
            existingIncident.setCarePlan(carePlanService.findById(incidentsDTO.getCarePlanID()).orElseThrow());
        } else {
            existingIncident.setCarePlan(null);
        }

        if (incidentsDTO.getAppointmentID() != null && !incidentsDTO.getAppointmentID().isBlank()) {
            existingIncident.setAppointment(appointmentService.getAppointmentByIdWithEntity(incidentsDTO.getAppointmentID()));
        } else {
            existingIncident.setAppointment(null);
        }

        if (incidentsDTO.getTaskID() != null && !incidentsDTO.getTaskID().isBlank()) {
            existingIncident.setTask(taskService.getTaskByIdWithEntity(incidentsDTO.getTaskID()));
        } else {
            existingIncident.setTask(null);
        }

        if (incidentsDTO.getIncidentTypeID() != null && !incidentsDTO.getIncidentTypeID().isBlank()) {
            existingIncident.setIncidentType(
                    incidentTypeService.getIncidentTypeByIdWithEntitiy(incidentsDTO.getIncidentTypeID()));
        }

        if (incidentsDTO.getIncidentStatusID() != null && !incidentsDTO.getIncidentStatusID().isBlank()) {
            existingIncident.setIncidentStatus(
                    incidentStatusService.getStatusByIdWithEntity(incidentsDTO.getIncidentStatusID()));
        }

        if (incidentsDTO.getAddress() != null) {
            if (existingIncident.getAppointmentAddress() != null) {
                AppointmentAddress existingAddress = existingIncident.getAppointmentAddress();
                existingAddress.setAddress(incidentsDTO.getAddress().getAddress());
                existingAddress.setCity(incidentsDTO.getAddress().getCity());
                existingAddress.setState(incidentsDTO.getAddress().getState());
                existingAddress.setPostalCode(incidentsDTO.getAddress().getPostalCode());
                appointmentAddressService.update(existingAddress);
            } else {
                AppointmentAddress savedAddress = appointmentAddressService.save(incidentsDTO.getAddress());
                existingIncident.setAppointmentAddress(savedAddress);
            }
        }

        incidentInvolvedPartiesService.deletePartiesByIncident(id);
        if (incidentsDTO.getParties() != null) {
            for (IncidentInvolvedPartiesDTO party : incidentsDTO.getParties()) {
                party.setIncidentID(id);
                incidentInvolvedPartiesService.createParty(party);
            }
        }

        incidentActionTypeAllAnswersService.deleteAllAnswersRelatedToIncident(id);
        if (incidentsDTO.getAnswers() != null) {
            for (IncidentActionTypeAllAnswersDTO answer : incidentsDTO.getAnswers()) {
                answer.setIncidentID(id);
                answer.setId(null); // force insert after delete-all
                incidentActionTypeAllAnswersService.saveAnswer(answer);
            }
        }

        try {
            List<IncidentDocumentsDTO> documents = incidentsDTO.getDocuments();
            if (documents != null && !documents.isEmpty()) {
                for (IncidentDocumentsDTO note : documents) {
                    IncidentDocumentsDTO noteExist = incidentDocumentServicel.getDocumentById(note.getDocumentID());
                    if (noteExist.getDocumentID() != null && !noteExist.getDocumentID().isEmpty()) {
                        incidentDocumentServicel.deleteDocument(noteExist.getDocumentID());
                        File directory = new File(noteExist.getDocumentLocation());
                        if (directory.exists()) {
                            directory.delete();
                        }
                    }
                }
            }

            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (file != null) {
                        String originalFileName = file.getOriginalFilename();
                        String fileExtension = originalFileName;
                        if (originalFileName != null && originalFileName.contains(".")) {
                            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                        }
                        File directory = new File(DIRECTORY);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        File destinationFile = new File(DIRECTORY, id + "_" + originalFileName + fileExtension);
                        if (destinationFile.exists()) {
                            boolean deleted = destinationFile.delete();
                            if (!deleted) {
                                throw new RuntimeException("Failed to delete the existing file: " + destinationFile.getName());
                            }
                        }
                        file.transferTo(destinationFile);
                        IncidentDocumentsDTO incidentDocumentsDTO = new IncidentDocumentsDTO();
                        incidentDocumentsDTO.setIncidentID(id);
                        incidentDocumentsDTO.setDocumentLocation(destinationFile.getAbsolutePath());
                        incidentDocumentsDTO.setDocumentName(originalFileName);
                        incidentDocumentServicel.saveDocument(incidentDocumentsDTO);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        incidentRepository.save(existingIncident);
        return getIncidentById(id);
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
        incidentsDTO.setEmployeeID(incident.getEmployee().getEmployeeID());
        incidentsDTO.setFollowUp(incident.getFollowUp());
        incidentsDTO.setNotes(incident.getNotes());
        incidentsDTO.setIssue(incident.getIssue());

        if (incident.getClient()!=null){
            incidentsDTO.setClientID(incident.getClient().getClientID());
        }

        if (incident.getAppointmentAddress()!=null){
            AppointmentAddressDTO appointmentAddressDTO = new AppointmentAddressDTO();
            AppointmentAddress appointmentAddress = incident.getAppointmentAddress();
            appointmentAddressDTO.setAppointmentAddressID(appointmentAddress.getAppointmentAddressID());
            appointmentAddressDTO.setAddress(appointmentAddress.getAddress());
            appointmentAddressDTO.setCity(appointmentAddress.getCity());
            appointmentAddressDTO.setPostalCode(appointmentAddress.getPostalCode());
            appointmentAddressDTO.setState(appointmentAddress.getState());
            incidentsDTO.setAddress(appointmentAddressDTO);
        }
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
        incident.setNotes(dto.getNotes());
        incident.setDischargeDate(dto.getDischargeDate());
        if (dto.getClientID()!=null){
            Client client = clientService.getClientByIdWithEntitiy(dto.getClientID());
            incident.setClient(client);
        }
        incident.setEmployee(employeeService.findOneEmployeeWithEntity(dto.getEmployeeID()));

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
