package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.util.List;

@Data
public class CarePlanLongTermGoalDTO {
    private String longGoalID;
    private String longTermGoal;
    private String achieveWay;
    private String supportWay;
    private String notes;
    private String careplanID;
    private List<CarePlanShortTermGoalDTO> carePlanShortTermGoals;
}
