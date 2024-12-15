package com.skycareSolutions.skyCarePortal.dto;


import lombok.Data;

@Data
public class JobAssignerDTO {
    private String assignID;
    private String careGiverID;
    private String taskID;
    private String appointmentID;
    private String assignType;
    private String assigner;
    private String acceptanceType;
    private String recurrentAppointmentID;
}
