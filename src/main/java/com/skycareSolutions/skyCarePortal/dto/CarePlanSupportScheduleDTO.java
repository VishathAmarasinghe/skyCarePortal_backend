package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class CarePlanSupportScheduleDTO {
    private String scheduleID;
    private String careplanID;
    private String day;
    private String supportType;
    private double hours;
    private double rate;
}

