package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.Appointment;
import com.skycareSolutions.skyCarePortal.model.Client;
import com.skycareSolutions.skyCarePortal.model.JobAssigner;
import com.skycareSolutions.skyCarePortal.repository.AppointmentRepository;
import com.skycareSolutions.skyCarePortal.repository.AppointmentTypesRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentAttachmentsService appointmentAttachmentsService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentTypeService appointmentTypeService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private AppointmentAddressService appointmentAddressService;

    @Autowired
    private CarePlanService carePlanService;

    @Autowired
    private JobAssignerService jobAssignerService;

    @Autowired
    private RecurrentWorkService recurrentWorkService;

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAllAppointments()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AppointmentDTO getAppointmentById(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        return mapToDTO(appointment);
    }

    public List<AppointmentDTO> getAppointmentsbyClientID(String clientID){
        Client clientInfo = clientService.getClientByIdWithEntitiy(clientID);
        if (clientInfo!=null){
            List<Appointment> appointments = appointmentRepository.findAppointmentsByClient(clientInfo);
            List<AppointmentDTO> appointmentDTOS = new ArrayList<>();
            for (Appointment app:appointments){
                appointmentDTOS.add(mapToDTO(app));
            }
            return appointmentDTOS;
        }else{
            throw new RuntimeException();
        }

    }


    public Appointment getAppointmentByIdWithEntity(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        return appointment;
    }



    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO, List<MultipartFile> files) {
        String lastAppointmentID = appointmentRepository.getLastIdOfAppointments();
        String newAppointmentID = Micro_utils.idGenerator("appointment",lastAppointmentID);
        appointmentDTO.setAppointmentID(newAppointmentID);

        if (appointmentDTO.getAppointmentAddress().getAddress()!=null && appointmentDTO.getAppointmentAddress().getAddress()!=""){
            appointmentAddressService.save(appointmentDTO.getAppointmentAddress());
        }
        Appointment appointment = mapToEntity(appointmentDTO);
        appointment = appointmentRepository.save(appointment);

        if (appointmentDTO.getRecurrenceState()){
            appointmentDTO.getRecurrentWork().setAppointmentID(newAppointmentID);
            recurrentWorkService.createRecurrentWork(appointmentDTO.getRecurrentWork());
        }

        appointmentDTO.getJobAssigns().setAppointmentID(newAppointmentID);
        jobAssignerService.createJobAssigner(appointmentDTO.getJobAssigns(),appointmentDTO.getBroadcastType());

        AppointmentAttachmentsDTO attachmentInfo = new AppointmentAttachmentsDTO();
        attachmentInfo.setAppointmentID(newAppointmentID);

        if (files!=null && files.size()>0){
            appointmentAttachmentsService.createAttachements(attachmentInfo,files);
        }


        return mapToDTO(appointment);
    }

    public AppointmentDTO updateAppointment(String id, AppointmentDTO appointmentDTO) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found!"));
        // Update entity fields
        appointment.setTitle(appointmentDTO.getTitle());
        // Other field updates here
        appointment = appointmentRepository.save(appointment);
        return mapToDTO(appointment);
    }

    public void deleteAppointment(String id) {
        appointmentRepository.deleteById(id);
    }

    // Map to DTO
    private AppointmentDTO mapToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setAppointmentID(appointment.getAppointmentID());
        dto.setTitle(appointment.getTitle());
        dto.setAppointmentTypeID(appointment.getAppointmentType().getAppointmentTypeID());
        dto.setClientID(appointment.getClient().getClientID());
        dto.setCaregiverCount(appointment.getCaregiverCount());
        dto.setStartDate(appointment.getStartDate());
        dto.setStartTime(appointment.getStartTime());
        dto.setEndDate(appointment.getEndDate());
        dto.setEndTime(appointment.getEndTime());
        dto.setDuration(appointment.getDuration());
        dto.setComment(appointment.getComment());
        dto.setBroadcastType(appointment.getBroadcastType());

        // Map AppointmentAddress
        if (appointment.getAppointmentAddress() != null) {
            AppointmentAddressDTO addressDTO = new AppointmentAddressDTO();
            addressDTO.setAppointmentAddressID(appointment.getAppointmentAddress().getAppointmentAddressID());
            addressDTO.setAddress(appointment.getAppointmentAddress().getAddress());
            addressDTO.setCity(appointment.getAppointmentAddress().getCity());
            addressDTO.setState(appointment.getAppointmentAddress().getState());
            addressDTO.setPostalCode(appointment.getAppointmentAddress().getPostalCode());
            dto.setAppointmentAddress(addressDTO);
        }

        if (appointment.getCarePlan()!=null){
            dto.setCarePlanID(appointment.getCarePlan().getCareplanID());
        }
        List<JobAssignerDTO> jobAssigner = jobAssignerService.getAllJobsRelatedToAppointment(appointment.getAppointmentID());
        JobAssignCreationDTO createDTO = new JobAssignCreationDTO();
        createDTO.setAppointmentID(appointment.getAppointmentID());

        String[] careGiverList = new String[jobAssigner.size()];

        int index = 0;
        for (JobAssignerDTO assigner : jobAssigner) {
            careGiverList[index] = assigner.getCareGiverID();
            index++;
        }
        createDTO.setCareGiverIDs(careGiverList);
        dto.setJobAssigns(createDTO);

        RecurrentWorkDTO rw = recurrentWorkService.getRecurrentWorkTaskByAppointment(appointment.getAppointmentID());

        if (rw!=null){
            dto.setRecurrenceState(true);
            dto.setRecurrentWork(rw);
        }
        List<AppointmentAttachmentsDTO> attachments = appointmentAttachmentsService.getAttachmentsbyAppointmentID(appointment.getAppointmentID());

        dto.setAttachments(attachments);
        return dto;
    }


    // Map to Entity
    private Appointment mapToEntity(AppointmentDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentID(dto.getAppointmentID());
        appointment.setTitle(dto.getTitle());
        appointment.setAppointmentType(appointmentTypeService.getAppointmentTypeByIdWithEntity(dto.getAppointmentTypeID()));

        Client client = clientService.getClientByIdWithEntitiy(dto.getClientID());
        appointment.setClient(client);

        // Set AppointmentAddress
        appointmentAddressService.findById(dto.getAppointmentAddress().getAppointmentAddressID())
                .ifPresentOrElse(
                        appointment::setAppointmentAddress,
                        () -> {
                            throw new RuntimeException("AppointmentAddress not found for ID: " + dto.getAppointmentAddress().getAppointmentAddressID());
                        }
                );

        appointment.setBroadcastType(dto.getBroadcastType());
        appointment.setComment(dto.getComment());
        appointment.setDuration(dto.getDuration());
        appointment.setStartDate(dto.getStartDate());
        appointment.setStartTime(dto.getStartTime());
        appointment.setEndDate(dto.getEndDate());
        appointment.setEndTime(dto.getEndTime());
        appointment.setCaregiverCount(dto.getCaregiverCount());
        if (dto.getCarePlanID()!=null && !dto.getCarePlanID().equalsIgnoreCase("")){
            System.out.println("care plan id is there "+dto.getCarePlanID());
            carePlanService.findById(dto.getCarePlanID())
                    .ifPresentOrElse(
                            appointment::setCarePlan,
                            () -> {
                                throw new RuntimeException("careplan not found for ID: " + dto.getCarePlanID());
                            }
                    );
        }

//        if (dto.getTaskID()!=null && dto.getTaskID().equalsIgnoreCase("")){
//            carePlanService.findById(dto.getCarePlanID())
//                    .ifPresentOrElse(
//                            appointment::setCarePlan,
//                            () -> {
//                                throw new RuntimeException("careplan not found for ID: " + dto.getCarePlanID());
//                            }
//                    );
//        }



        // Map other fields
        return appointment;
    }
}
