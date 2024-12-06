package com.skycareSolutions.skyCarePortal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class IncidentsDTO {
    private String incidentID;
    private String title;
    private LocalDate reportDate;
    private LocalDate incidentDate;
    private LocalDateTime incidentTime;
    private String incidentTypeID;
    private String incidentStatusID;
    private AppointmentAddressDTO address;
    private String issue;
    private String description;
    private boolean hospitalized;
    private LocalDate admitDate;
    private LocalDate dischargeDate;
    private String carePlanID;
    private String appointmentID;
    private String taskID;
    private String followUp;
    private String notes;
}
