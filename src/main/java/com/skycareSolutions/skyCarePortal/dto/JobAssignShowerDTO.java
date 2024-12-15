package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class JobAssignShowerDTO {
    private String recurrentAppointmentID;
    private String appointmentID;
    private String jobState;
    private LocalDate startDate;
    private String startTime;
    private LocalDate endDate;
    private String endTime;
    private boolean cancelState;
    private int headCount;
    private String comment;
    private List<CareGiverAssignedDTO> assigners;
}
