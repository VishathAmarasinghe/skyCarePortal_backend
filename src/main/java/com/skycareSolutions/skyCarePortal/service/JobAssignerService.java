package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.JobAssignerRepository;
import com.skycareSolutions.skyCarePortal.repository.ShiftNoteRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class JobAssignerService {

    @Autowired
    private JobAssignerRepository jobAssignerRepository;

    @Autowired
    @Lazy
    private AppointmentService appointmentService;

    @Autowired
    @Lazy
    private EmployeeService employeeService;

    @Autowired
    @Lazy
    private CareGiverService careGiverService;

    @Autowired
    @Lazy
    private RecurrenceService recurrenceService;

    @Autowired
    @Lazy
    private EmailService emailService;

    @Autowired
    @Lazy
    private ShiftNoteRepository shiftNoteRepository;

    @Value("${app.external.service.frontendUrl}")
    private String frontendUrl;

    // Map DTO to Entity
    private JobAssigner mapToEntity(JobAssignerDTO dto) {
        JobAssigner entity = new JobAssigner();
        entity.setAssignID(dto.getAssignID());
        entity.setRecurrentAppointment(recurrenceService.getRecurrentAppointmentByIDWithEntity(dto.getRecurrentAppointmentID()));
        entity.setAcceptanceType(dto.getAcceptanceType());
        if (dto.getAppointmentID()!=null && dto.getAppointmentID()!=""){
            entity.setAppointment(appointmentService.getAppointmentByIdWithEntity(dto.getAppointmentID()));
        }
        entity.setEmployee(employeeService.findOneEmployeeWithEntity(dto.getAssigner()));
        entity.setCareGiver(careGiverService.getCareGiverByIdWithEntity(dto.getCareGiverID()));
        entity.setAssignType(dto.getAssignType());
        return entity;
    }

    // Map Entity to DTO
    private JobAssignerDTO mapToDTO(JobAssigner entity) {
        JobAssignerDTO dto = new JobAssignerDTO();
        dto.setAssignID(entity.getAssignID());
        if (entity.getCareGiver()!=null){
            dto.setCareGiverID(entity.getCareGiver().getCareGiverID());
        }

        dto.setAssigner(entity.getEmployee().getEmployeeID());
        dto.setAssignType(entity.getAssignType());
        dto.setRecurrentAppointmentID(entity.getRecurrentAppointment().getRecurrentAppointmentID());
        dto.setAcceptanceType(entity.getAcceptanceType());
        if (entity.getAppointment()!=null){
            dto.setAppointmentID(entity.getAppointment().getAppointmentID());
        }
        return dto;
    }

    // Create a new JobAssigner
    public void createJobAssigner(JobAssignCreationDTO dto, String broadcastType) {
        List<RecurrentAppointmentDTO> recurrentAppointmentDTOList = recurrenceService.getRecurrentAppointmentsByAppointmentID(dto.getAppointmentID());
        List<String> careGiverSendList = new ArrayList<>();
        if (broadcastType.equalsIgnoreCase("All Caregivers")){
                List<CareGiverDTO> careGivers = careGiverService.getAllCareGivers();
                for (CareGiverDTO careGiver : careGivers) {
                    for (RecurrentAppointmentDTO recurrentAppointmentDTO:recurrentAppointmentDTOList){
                        String lastAssignID = jobAssignerRepository.getLastIdOfJobAssigner();
                        String newAssignID = Micro_utils.idGenerator("jobAssigner", lastAssignID);
                        JobAssignerDTO jobAssign = new JobAssignerDTO();
                        jobAssign.setAssignID(newAssignID);
                        jobAssign.setAssignType(dto.getAssignType());
                        jobAssign.setAppointmentID(dto.getAppointmentID());
                        jobAssign.setAssigner(dto.getAssigner());
                        jobAssign.setAcceptanceType("Allocated");
                        jobAssign.setCareGiverID(careGiver.getCareGiverID());
                        jobAssign.setRecurrentAppointmentID(recurrentAppointmentDTO.getRecurrentAppointmentID());
                        JobAssigner entity = mapToEntity(jobAssign);
                        JobAssigner savedEntity = jobAssignerRepository.save(entity);

                        if (!careGiverSendList.contains(careGiver.getEmployee().getEmployeeID())) {
                            careGiverSendList.add(careGiver.getEmployee().getEmployeeID()); // Add to the list to prevent duplicate emails
                            CompletableFuture.runAsync(() -> emailService.generateAppointmentConfirmationEmail(
                                    careGiver.getEmployee().getFirstName(),
                                    careGiver.getEmployee().getEmail(),
                                    frontendUrl
                            ));
                        }
                    }
                }
        }else{
            for (String careGiverID:dto.getCareGiverIDs()){
                for (RecurrentAppointmentDTO recurrentAppointmentDTO:recurrentAppointmentDTOList){
                    System.out.println("logging caregivers "+careGiverID);
                    CareGiver careGiver = careGiverService.getCareGiverByIdWithEntity(careGiverID);
                    String lastAssignID = jobAssignerRepository.getLastIdOfJobAssigner();
                    String newAssignID = Micro_utils.idGenerator("jobAssigner", lastAssignID);
                    JobAssignerDTO jobAssign = new JobAssignerDTO();
                    jobAssign.setAssignID(newAssignID);
                    jobAssign.setAssignType(dto.getAssignType());
                    jobAssign.setAppointmentID(dto.getAppointmentID());
                    jobAssign.setAssigner(dto.getAssigner());
                    jobAssign.setAcceptanceType("Allocated");
                    jobAssign.setCareGiverID(careGiverID);
                    jobAssign.setRecurrentAppointmentID(recurrentAppointmentDTO.getRecurrentAppointmentID());
                    JobAssigner entity = mapToEntity(jobAssign);
                    JobAssigner savedEntity = jobAssignerRepository.save(entity);

                    if (!careGiverSendList.contains(careGiver.getEmployee().getEmployeeID())) {
                        careGiverSendList.add(careGiver.getEmployee().getEmployeeID()); // Add to the list to prevent duplicate emails
                        CompletableFuture.runAsync(() -> emailService.generateAppointmentConfirmationEmail(
                                careGiver.getEmployee().getFirstName(),
                                careGiver.getEmployee().getEmail(),
                                frontendUrl
                        ));
                    }
                }
            }
        }
    }

    // Get all JobAssigners
    public List<JobAssignerDTO> getAllJobAssigners() {
        return ((List<JobAssigner>) jobAssignerRepository.findAll())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<String> getAllClientIDListCareGiverAssociated(String employeeID){

        CareGiverDTO careGiver = careGiverService.getCareGiverByEmployee(employeeID);
        CareGiver careGiverInfo = careGiverService.getCareGiverByIdWithEntity(careGiver.getCareGiverID());
        List<JobAssigner> jobList = jobAssignerRepository.findJobsAssignedToCareGiver(careGiverInfo);
        Set<String> clientList = new HashSet<>();
        for (JobAssigner job:jobList){
            clientList.add(job.getRecurrentAppointment().getAppointment().getClient().getClientID());
        }
        return clientList.stream().toList();
    }

    public List<JobAssignCareGiverViewerDTO> jobAssignTOCareGivers(String id){
        CareGiverDTO careGiverDTO = careGiverService.getCareGiverByEmployee(id);
        Employee employee = employeeService.findOneEmployeeWithEntity(id);
        CareGiver careGiver = careGiverService.getCareGiverByIdWithEntity(careGiverDTO.getCareGiverID());
        List<JobAssigner> jobs = jobAssignerRepository.findJobsAssignedToCareGiver(careGiver);
        List<JobAssignCareGiverViewerDTO> assignViewer = new ArrayList<>();
        for (JobAssigner job:jobs){

            ShiftNotes shiftNote = shiftNoteRepository.getShiftNotesByRecurrentAppointmentAndEmployee(job.getRecurrentAppointment(),employee);
            if (shiftNote==null && !job.getAcceptanceType().equalsIgnoreCase("Rejected")){
                JobAssignCareGiverViewerDTO singleView = new JobAssignCareGiverViewerDTO();
                singleView.setAssignJob(mapToDTO(job));
                AppointmentDTO appointment = appointmentService.getAppointmentById(job.getAppointment().getAppointmentID());
                singleView.setAppointment(appointment);
                RecurrentAppointmentDTO recurrentAppointment = recurrenceService.getRecurrentAppointmentByID(job.getRecurrentAppointment().getRecurrentAppointmentID());
                singleView.setRecurrentAppointment(recurrentAppointment);
                    assignViewer.add(singleView);


            }

        }
        return assignViewer;
    }

    public List<JobAssignCareGiverViewerDTO> allJobsAssignedToCareGivers(String id){
        CareGiverDTO careGiverDTO = careGiverService.getCareGiverByEmployee(id);
        Employee employee = employeeService.findOneEmployeeWithEntity(id);
        CareGiver careGiver = careGiverService.getCareGiverByIdWithEntity(careGiverDTO.getCareGiverID());
        List<JobAssigner> jobs = jobAssignerRepository.findJobsAssignedToCareGiver(careGiver);
        List<JobAssignCareGiverViewerDTO> assignViewer = new ArrayList<>();
        for (JobAssigner job:jobs){
                JobAssignCareGiverViewerDTO singleView = new JobAssignCareGiverViewerDTO();
                singleView.setAssignJob(mapToDTO(job));
                AppointmentDTO appointment = appointmentService.getAppointmentById(job.getAppointment().getAppointmentID());
                singleView.setAppointment(appointment);
                RecurrentAppointmentDTO recurrentAppointment = recurrenceService.getRecurrentAppointmentByID(job.getRecurrentAppointment().getRecurrentAppointmentID());
                singleView.setRecurrentAppointment(recurrentAppointment);
                assignViewer.add(singleView);

        }
        return assignViewer;
    }

    public Boolean updateAssigners(UpdateAssignersDTO updateAssignersDTO){
        List<String> newComers = updateAssignersDTO.getAdded();
        List<String> removers = updateAssignersDTO.getDeleted();
        System.out.println("new commer "+newComers.toString());
        RecurrentAppointment recurrentAppointmentDTO = recurrenceService.getRecurrentAppointmentByIDWithEntity(updateAssignersDTO.getRecurrentAppointmentID());
        if (recurrentAppointmentDTO!=null){
            JobAssignerDTO assigner = new JobAssignerDTO();
            assigner.setAppointmentID(recurrentAppointmentDTO.getAppointment().getAppointmentID());
            assigner.setRecurrentAppointmentID(recurrentAppointmentDTO.getRecurrentAppointmentID());
            assigner.setAssigner(updateAssignersDTO.getAssignerID());
            assigner.setAssignType("");
            assigner.setAcceptanceType("Allocated");
            for (String newComer:newComers){
                String lastAssignID = jobAssignerRepository.getLastIdOfJobAssigner();
                String newAssignID = Micro_utils.idGenerator("jobAssigner", lastAssignID);
                assigner.setAssignID(newAssignID);
                assigner.setCareGiverID(newComer);
                jobAssignerRepository.save(mapToEntity(assigner));
            }
        }
        for(String remove:removers){
            CareGiver careGiver = careGiverService.getCareGiverByIdWithEntity(remove);
            JobAssigner assiner = jobAssignerRepository.findJobAssignWithRecurrentAppointmentAndCareGiver(recurrentAppointmentDTO,careGiver);
            jobAssignerRepository.deleteById(assiner.getAssignID());
        }
        return true;
    }

    public JobAssignerDTO findJobAssignerByCareGiverIDAndRecurrentID(String careGiverID,String recurrentID){
        CareGiver careGiver = careGiverService.getCareGiverByIdWithEntity(careGiverID);
        RecurrentAppointment recurrentAppointment = recurrenceService.getRecurrentAppointmentByIDWithEntity(recurrentID);
        JobAssigner assigner = jobAssignerRepository.findJobAssignWithRecurrentAppointmentAndCareGiver(recurrentAppointment,careGiver);
        return mapToDTO(assigner);
    }

    public List<JobAssignerDTO> findJobAssignerByRecurrentID(String recurrentID){
        RecurrentAppointment recurrentAppointment = recurrenceService.getRecurrentAppointmentByIDWithEntity(recurrentID);
        List<JobAssigner> assigner = jobAssignerRepository.findJobAssignWithRecurrentAppointment(recurrentAppointment);
        return assigner.stream().map(this::mapToDTO).toList();
    }

    public List<JobAssignShowerDTO> jobAssignerShower(String appointmentID){
        List<RecurrentAppointmentDTO> recurrentAppointment = recurrenceService.getRecurrentAppointmentsByAppointmentID(appointmentID);
        List<JobAssignShowerDTO> jobAssignShowerDTOList = new ArrayList<>();
        for (RecurrentAppointmentDTO recurrentAppointmentDTO:recurrentAppointment){
            RecurrentAppointment recurrentAppointment1 = recurrenceService.getRecurrentAppointmentByIDWithEntity(recurrentAppointmentDTO.getRecurrentAppointmentID());
            List<JobAssigner> jobAssigns = jobAssignerRepository.findAllJobAssignersByRecurrentAppointmentID(recurrentAppointment1);
            System.out.println("job assign length "+jobAssigns.size());
            JobAssignShowerDTO jobAssignShowerDTO = new JobAssignShowerDTO();
            List<CareGiverAssignedDTO> careGiverAssignedDTOS = new ArrayList<>();
            for (JobAssigner jb:jobAssigns){
                System.out.println("job assingers "+jb.getCareGiver().getEmployee().getFirstName());
                CareGiverAssignedDTO careGiverAssignedDTO = new CareGiverAssignedDTO();
                careGiverAssignedDTO.setAcceptanceState(jb.getAcceptanceType());
                careGiverAssignedDTO.setCareGiverID(jb.getCareGiver().getCareGiverID());
                CareGiver careGiver = careGiverService.getCareGiverByIdWithEntity(jb.getCareGiver().getCareGiverID());
                careGiverAssignedDTO.setEmployeeID(careGiver.getEmployee().getEmployeeID());
                careGiverAssignedDTO.setFirstName(careGiver.getEmployee().getFirstName());
                careGiverAssignedDTO.setLastName(careGiver.getEmployee().getLastName());
                careGiverAssignedDTO.setUserProfilePhoto(careGiver.getEmployee().getProfile_photo());
                careGiverAssignedDTOS.add(careGiverAssignedDTO);

            }
            jobAssignShowerDTO.setAssigners(careGiverAssignedDTOS);
            jobAssignShowerDTO.setJobState(recurrentAppointmentDTO.getStatus());
            jobAssignShowerDTO.setStartDate(recurrentAppointmentDTO.getStartDate());
            jobAssignShowerDTO.setEndDate(recurrentAppointmentDTO.getEndDate());
            jobAssignShowerDTO.setStartTime(recurrentAppointmentDTO.getStartTime());
            jobAssignShowerDTO.setEndTime(recurrentAppointmentDTO.getEndTime());
            jobAssignShowerDTO.setRecurrentAppointmentID(recurrentAppointmentDTO.getRecurrentAppointmentID());
            jobAssignShowerDTO.setCancelState(recurrentAppointment1.isCancelled());
            jobAssignShowerDTO.setComment(recurrentAppointment1.getComment());
            jobAssignShowerDTO.setHeadCount(recurrentAppointment1.getAppointment().getCaregiverCount());
            jobAssignShowerDTO.setAppointmentID(recurrentAppointment1.getAppointment().getAppointmentID());
            jobAssignShowerDTOList.add(jobAssignShowerDTO);
        }
        return jobAssignShowerDTOList;
    }

    // Get a JobAssigner by ID
    public Optional<JobAssignerDTO> getJobAssignerById(String id) {
        return jobAssignerRepository.findById(id).map(this::mapToDTO);
    }



    public int careGiverCountByAppointmentAcceptanceType(String appointmentID,String status,String recurrnetAppointmentID){
        Appointment appointment = appointmentService.getAppointmentByIdWithEntity(appointmentID);
        RecurrentAppointment recurrentAppointment = recurrenceService.getRecurrentAppointmentByIDWithEntity(recurrnetAppointmentID);
        return jobAssignerRepository.findNumberOfJobAssignersBasedOnAcceptanceType(appointment,status,recurrentAppointment);
    }

    // Update a JobAssigner
    public Optional<JobAssignerDTO> updateJobAssigner(String id, JobAssignerDTO dto) {
        return jobAssignerRepository.findById(id).map(existing -> {
            existing.setAssignType(dto.getAssignType());
            existing.setAcceptanceType(dto.getAcceptanceType());
            // Map and update other fields as needed
            JobAssigner updatedEntity = jobAssignerRepository.save(existing);
            return mapToDTO(updatedEntity);
        });
    }

    public List<JobAssignerDTO> assignedJobsBasedOnCareGiverAndStatus(String careGiverid,String status){
        CareGiver careGiver = careGiverService.getCareGiverByIdWithEntity(careGiverid);
        if (careGiver!=null){
            List<JobAssigner> jobs = jobAssignerRepository.findJobsAssignedToCareGiverBasedOnStatus(careGiver,status);
            List<JobAssignerDTO> jobDTOs = new ArrayList<>();
            for (JobAssigner job:jobs){
                jobDTOs.add(mapToDTO(job));
            }
            return jobDTOs;
        }else{
            throw new RuntimeException();
        }
    }

    public boolean updateJobAcceptanceState(String state,String assignID){
        if (jobAssignerRepository.updateAcceptanceType(assignID,state)>0){
            return true;
        }else{
            return false;
        }
    }


    public List<JobAssignerDTO> getAllJobsRelatedToAppointment(String appointmentID){
        Appointment app = appointmentService.getAppointmentByIdWithEntity(appointmentID);
        List<JobAssigner> jb = jobAssignerRepository.findJobAssignersByAppointment(app);
        List<JobAssignerDTO> jobAssignerDTO = new ArrayList<>();
        for (JobAssigner job:jb){
            jobAssignerDTO.add((mapToDTO(job)));
        }
        return jobAssignerDTO;
    }

    // Delete a JobAssigner
    public boolean deleteJobAssigner(String id) {
        if (jobAssignerRepository.existsById(id)) {
            jobAssignerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

