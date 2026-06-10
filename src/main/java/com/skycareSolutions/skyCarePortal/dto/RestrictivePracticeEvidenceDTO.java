package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class RestrictivePracticeEvidenceDTO {
    private String evidenceID;
    private String clientID;
    private String participantName;
    private String restrictivePracticeType;
    private String baselineFrequency;
    private String currentFrequency;
    private String reductionGoal;
    private String strategiesImplemented;
    private String alternativeSupportsUsed;
    private boolean staffTrainingCompleted;
    private boolean behaviourDataReviewed;
    private String reviewDate;
    private String outcomeProgress;
    private String nextActions;
    private String responsibleEmployeeID;
    private String responsiblePersonName;
}
