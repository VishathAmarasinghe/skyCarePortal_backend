package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class StaffTrainingRecordDTO {
    private String recordID;
    private String employeeID;
    private String staffNameManual;
    private String employeeName;
    private String position;
    private String trainingCourse;
    private String provider;
    private String dateCompleted;
    private String expiryDate;
    private boolean certificateFiled;
    private boolean hasCertificate;
    private boolean clearCertificate;
    private boolean competencyAssessed;
    private String assessorEmployeeID;
    private String assessorNameManual;
    private String assessorName;
    private String comments;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String expiryStatus;
}
