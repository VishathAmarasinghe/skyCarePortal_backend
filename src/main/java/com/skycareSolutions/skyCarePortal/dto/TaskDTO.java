package com.skycareSolutions.skyCarePortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private String taskID;
    private String clientID;
    private int careGiverCount;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
    private String carePlanID;
    private int duration;
    private String comment;
    private String broadcastType;
    private String taskStatusID;
    private String taskImportanceID;
    private AppointmentAddressDTO appointmentAddress;
    private List<TaskItemDTO> taskItems;
}
