package com.skycareSolutions.skyCarePortal.dto;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CarePlanDTO {
    private String title;
    private String careplanID;
    private String carePlanStatusID;
    private String startDate;
    private String endDate;
    private String clientID;
    private List<CarePlanLongTermGoalDTO> carePlanLongTermGoals;
    private List<CarePlanBilablesDTO> carePlanBillables;
    private String currentLivingSituation;
    private String supportsFundedCore;
    private String supportsFundedCapacityBuilding;
    private String supportsFundedCapital;
    private List<CarePlanSupportScheduleDTO> supportSchedule;
    private String riskManagement;
    private String expectedOutcomes;
    private String billingBudgetNotes;
    private String status;
}
