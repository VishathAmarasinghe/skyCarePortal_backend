package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.util.List;

@Data
public class RestrictivePracticeRegisterDTO {
    private String registerID;
    private String clientID;
    private String participantName;
    private String ndisNumber;
    private String restrictivePracticeType;
    private String description;
    private boolean behaviourSupportPlanCurrent;
    private String authorisationStatus;
    private String authorisingBody;
    private String dateAuthorised;
    private String reviewDate;
    private String practitionerEmployeeID;
    private String practitionerName;
    private boolean reductionStrategyInPlace;
    private List<String> incidentIds;
    private boolean staffTrained;
    private String comments;
}
