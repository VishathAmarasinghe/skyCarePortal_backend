package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.AppointmentDTO;
import com.skycareSolutions.skyCarePortal.model.Appointment;
import com.skycareSolutions.skyCarePortal.repository.AppointmentRepository;
import com.skycareSolutions.skyCarePortal.repository.AppointmentTypesRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentTypeService appointmentTypeService;

    @Autowired
    private AppointmentAddressService appointmentAddressService;

    @Autowired
    private CarePlanService carePlanService;

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

    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO) {
        String lastAppointmentID = appointmentRepository.getLastIdOfAppointments();
        String newAppointmentID = Micro_utils.idGenerator("appointment",lastAppointmentID);
        appointmentDTO.setAppointmentID(newAppointmentID);
        Appointment appointment = mapToEntity(appointmentDTO);
        appointment = appointmentRepository.save(appointment);
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
        // Map other fields
        return dto;
    }

    // Map to Entity
    private Appointment mapToEntity(AppointmentDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentID(dto.getAppointmentID());
        appointment.setTitle(dto.getTitle());
        appointment.setAppointmentType(appointmentTypeService.getAppointmentTypeByIdWithEntity(dto.getAppointmentTypeID()));

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
        appointment.setEndDate(dto.getEndDate());
        appointment.setEndTime(dto.getEndTime());

        if (dto.getCarePlanID()!=null && dto.getCarePlanID().equalsIgnoreCase("")){
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
