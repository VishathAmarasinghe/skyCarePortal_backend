package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class EmployeeBasicInfoUpdaterDTO {
    private String employeeID;
    private String firstName;
    private String lastName;
    private String email;
    private String currentPassword;
    private String newPassword;
}
