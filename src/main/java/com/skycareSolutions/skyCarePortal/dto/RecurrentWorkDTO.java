package com.skycareSolutions.skyCarePortal.dto;
import java.time.LocalDate;

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
