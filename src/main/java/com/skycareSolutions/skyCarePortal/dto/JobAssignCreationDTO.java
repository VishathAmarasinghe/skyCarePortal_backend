package com.skycareSolutions.skyCarePortal.dto;


import lombok.Data;

@Data
public class JobAssignCreationDTO {
    private String[] careGiverIDs;
    private String taskID;
    private String appointmentID;
    private String assignType;
    private String assigner;
}
