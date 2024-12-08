package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.CareGiverDTO;
import com.skycareSolutions.skyCarePortal.dto.JobAssignCreationDTO;
import com.skycareSolutions.skyCarePortal.dto.JobAssignerDTO;
import com.skycareSolutions.skyCarePortal.model.Appointment;
import com.skycareSolutions.skyCarePortal.model.CareGiver;
import com.skycareSolutions.skyCarePortal.model.JobAssigner;
import com.skycareSolutions.skyCarePortal.repository.JobAssignerRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    // Map DTO to Entity
    private JobAssigner mapToEntity(JobAssignerDTO dto) {
        JobAssigner entity = new JobAssigner();
        entity.setAssignID(dto.getAssignID());
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
        dto.setAcceptanceType(entity.getAcceptanceType());
        if (entity.getAppointment()!=null){
            dto.setAppointmentID(entity.getAppointment().getAppointmentID());
        }
        return dto;
    }

    // Create a new JobAssigner
    public void createJobAssigner(JobAssignCreationDTO dto, String broadcastType) {
        if (broadcastType.equalsIgnoreCase("All Caregivers")){
                List<CareGiverDTO> careGivers = careGiverService.getAllCareGivers();
                for (CareGiverDTO careGiver : careGivers) {
                    String lastAssignID = jobAssignerRepository.getLastIdOfJobAssigner();
                    String newAssignID = Micro_utils.idGenerator("jobAssigner", lastAssignID);
                    JobAssignerDTO jobAssign = new JobAssignerDTO();
                    jobAssign.setAssignID(newAssignID);
                    jobAssign.setAssignType(dto.getAssignType());
                    jobAssign.setAppointmentID(dto.getAppointmentID());
                    jobAssign.setAssigner(dto.getAssigner());
                    jobAssign.setAcceptanceType("Allocated");
                    jobAssign.setCareGiverID(careGiver.getCareGiverID());
                    JobAssigner entity = mapToEntity(jobAssign);
                    JobAssigner savedEntity = jobAssignerRepository.save(entity);
                }
        }else{
            for (String careGiverID:dto.getCareGiverIDs()){
                System.out.println("logging caregivers "+careGiverID);
                String lastAssignID = jobAssignerRepository.getLastIdOfJobAssigner();
                String newAssignID = Micro_utils.idGenerator("jobAssigner", lastAssignID);
                JobAssignerDTO jobAssign = new JobAssignerDTO();
                jobAssign.setAssignID(newAssignID);
                jobAssign.setAssignType(dto.getAssignType());
                jobAssign.setAppointmentID(dto.getAppointmentID());
                jobAssign.setAssigner(dto.getAssigner());
                jobAssign.setAcceptanceType("Allocated");
                jobAssign.setCareGiverID(careGiverID);
                JobAssigner entity = mapToEntity(jobAssign);
                JobAssigner savedEntity = jobAssignerRepository.save(entity);
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

    // Get a JobAssigner by ID
    public Optional<JobAssignerDTO> getJobAssignerById(String id) {
        return jobAssignerRepository.findById(id).map(this::mapToDTO);
    }

    public int careGiverCountByAppointmentAcceptanceType(String appointmentID,String status){
        Appointment appointment = appointmentService.getAppointmentByIdWithEntity(appointmentID);
        return jobAssignerRepository.findNumberOfJobAssignersBasedOnAcceptanceType(appointment,status);
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

