package com.skycareSolutions.skyCarePortal.dto;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RecurrentWorkDTO {
    private String recurrentWorkID;
    private String appointmentID;
    private String taskID;
    private String recurrenceType;
    private LocalDate startDate;
    private LocalDate endDate;
    private int frequencyCount;
    private String day;
    private int occurrenceLimit;
}
