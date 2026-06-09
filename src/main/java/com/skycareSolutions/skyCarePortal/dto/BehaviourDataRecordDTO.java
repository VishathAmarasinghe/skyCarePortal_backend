package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class BehaviourDataRecordDTO {
    private String recordID;
    private String clientID;
    private String participantName;
    private String recordDate;
    private String recordTime;
    private String behaviourObserved;
    private String antecedent;
    private String staffResponse;
    private String restrictivePracticeUsed;
    private String outcome;
    private String duration;
    private String intensity;
    private boolean followUpRequired;
    private String staffEmployeeID;
    private String staffName;
    private String staffInitials;
}
