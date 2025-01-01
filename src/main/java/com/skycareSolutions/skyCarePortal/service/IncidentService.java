package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.AppointmentAddress;
import com.skycareSolutions.skyCarePortal.model.Client;
import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.model.Incidents;
import com.skycareSolutions.skyCarePortal.repository.IncidentRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
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
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<IncidentsDTO> getAllIncidentsByEmployeeID(String employeeID){
        Employee employee = employeeService.findOneEmployeeWithEntity(employeeID);
        List<Incidents> incidents = incidentRepository.getIncidentsRelatedToEmployee(employee);
        List<IncidentsDTO> dtoList = new ArrayList<>();
        for (Incidents inc:incidents){
            dtoList.add(convertToDTO(inc));
        }
        return dtoList;
    }

    // Get an incident by ID
    public IncidentsDTO getIncidentById(String id) {
        Incidents incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with ID: " + id));
        IncidentsDTO incidentValues = convertToDTO(incident);
        incidentValues.setParties(incidentInvolvedPartiesService.getPartiesByIncident(id));
        incidentValues.setDocuments(incidentDocumentServicel.getDocumentsRelatedToIncident(id));
        incidentValues.setAnswers(incidentActionTypeAllAnswersService.getAnswersByIncident(id));
        return incidentValues;
    }

    // Create a new incident
    public IncidentsDTO createIncident(IncidentsDTO incidentsDTO, List<MultipartFile> files) {
        String lastIncidentID = incidentRepository.getLastIdOfIncident();
        String newIncidentID = Micro_utils.idGenerator("incidents",lastIncidentID);
        incidentsDTO.setIncidentID(newIncidentID);

        AppointmentAddress appointmentAddress =  appointmentAddressService.save(incidentsDTO.getAddress());
        Incidents incident = convertToEntity(incidentsDTO);
        incident.setAppointmentAddress(appointmentAddress);

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
    public IncidentsDTO updateIncident(String id, IncidentsDTO incidentsDTO,List<MultipartFile> files) {
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

        AppointmentAddressDTO appointmentAddressDTO = new AppointmentAddressDTO();
        appointmentAddressDTO.setAppointmentAddressID(existingIncident.getAppointmentAddress().getAppointmentAddressID());
        appointmentAddressDTO.setCity(incidentsDTO.getAddress().getCity());
        appointmentAddressDTO.setState(incidentsDTO.getAddress().getState());
        appointmentAddressDTO.setAddress(incidentsDTO.getAddress().getAddress());
        appointmentAddressDTO.setPostalCode(incidentsDTO.getAddress().getPostalCode());
        appointmentAddressService.save(appointmentAddressDTO);


        existingIncident.getIncidentType().setIncidentTypeID(incidentsDTO.getIncidentTypeID());
        existingIncident.getIncidentStatus().setIncidentStatusID(incidentsDTO.getIncidentStatusID());

        incidentInvolvedPartiesService.deletePartiesByIncident(id);

        for(IncidentInvolvedPartiesDTO party:incidentsDTO.getParties()){
            party.setIncidentID(id);
            incidentInvolvedPartiesService.createParty(party);
        }

        incidentActionTypeAllAnswersService.deleteAllAnswersRelatedToIncident(id);
        for (IncidentActionTypeAllAnswersDTO answer:incidentsDTO.getAnswers()){
            answer.setIncidentID(id);
            incidentActionTypeAllAnswersService.saveAnswer(answer);
        }

        try {
            List<IncidentDocumentsDTO> documents = incidentsDTO.getDocuments();
            for (IncidentDocumentsDTO note:documents){
                IncidentDocumentsDTO noteExist = incidentDocumentServicel.getDocumentById(note.getDocumentID());
                if (noteExist.getDocumentID()!=null && !noteExist.getDocumentID().isEmpty()){
                    incidentDocumentServicel.deleteDocument(noteExist.getDocumentID());
                    File directory = new File(noteExist.getDocumentLocation());
                    if (directory.exists()){
                        directory.delete();
                    }
                }
            }
            if (files!=null && !files.isEmpty()){
                for (MultipartFile file:files){
                    System.out.println("lenght "+files.size());
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
                        File destinationFile = new File(DIRECTORY, id+"_"+originalFileName+ fileExtension);
                        if (destinationFile.exists()) {
                            // Delete the existing file
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
        incidentsDTO.setEmployeeID(incident.getEmployee().getEmployeeID());
        incidentsDTO.setFollowUp(incident.getFollowUp());
        incidentsDTO.setNotes(incident.getNotes());
        incidentsDTO.setIssue(incident.getIssue());

        if (incident.getClient()!=null){
            incidentsDTO.setClientID(incident.getClient().getClientID());
        }

        AppointmentAddressDTO appointmentAddressDTO = new AppointmentAddressDTO();
        AppointmentAddress appointmentAddress = incident.getAppointmentAddress();
        appointmentAddressDTO.setAddress(appointmentAddress.getAddress());
        appointmentAddressDTO.setCity(appointmentAddress.getCity());
        appointmentAddressDTO.setPostalCode(appointmentAddress.getPostalCode());
        appointmentAddressDTO.setState(appointmentAddress.getState());

        incidentsDTO.setAddress(appointmentAddressDTO);
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
