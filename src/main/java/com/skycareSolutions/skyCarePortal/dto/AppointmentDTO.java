package com.skycareSolutions.skyCarePortal.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AppointmentDTO {
    private String appointmentID;
    private String appointmentTypeID;
    private String clientID;
    private int caregiverCount;
    private LocalDate startDate;
    private LocalDateTime startTime;
    private LocalDate endDate;
    private LocalDateTime endTime;
    private int duration;
    private String comment;
    private String carePlanID;
    private String taskID;
    private String broadcastType;
    private String appointmentAddressID;
}
