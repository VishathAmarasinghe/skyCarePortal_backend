package com.skycareSolutions.skyCarePortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecurrentAppointmentDTO {
    private String recurrentAppointmentID;
    private String appointmentID;
    private String recurrentWorkID;
    private int occurrenceNumber;
    private LocalDate startDate;
    private String startTime;
    private LocalDate endDate;
    private String endTime;
    private String status;
    private boolean isCancelled;
    private String comment;
}
