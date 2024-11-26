package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class CarePlanShortTermGoalDTO {
    private String shortTermGoalID;
    private String longTermGoalID;
    private String goalTitle;
    private String goalDescription;
    private String goalOutcomeTypeID;
    private String outcomeDetails;
    private String goalStrategy;
    private String careplanID;
}

