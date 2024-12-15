package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class CareGiverAssignedDTO {
    private String firstName;
    private String LastName;
    private String employeeID;
    private String careGiverID;
    private String acceptanceState;
    private String userProfilePhoto;
}
