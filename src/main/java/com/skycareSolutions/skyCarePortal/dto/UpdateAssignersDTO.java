package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateAssignersDTO {
    private List<String> added;
    private List<String> deleted;
    private String recurrentAppointmentID;
    private String assignerID;
}
