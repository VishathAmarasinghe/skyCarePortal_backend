package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AppointmentDTO {
    private String appointmentID;
    private String title;
    private String appointmentTypeID;
    private String clientID;
    private int caregiverCount;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private int duration;
    private String comment;
    private String carePlanID;
    private String taskID;
    private String broadcastType;
    private AppointmentAddressDTO appointmentAddress;
    private List<AppointmentAttachmentsDTO> attachments;
    private Boolean recurrenceState;
    private RecurrentWorkDTO recurrentWork;
    private JobAssignCreationDTO jobAssigns;
}
