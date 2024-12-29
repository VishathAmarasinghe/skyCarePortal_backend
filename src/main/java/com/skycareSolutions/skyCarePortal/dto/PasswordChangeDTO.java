package com.skycareSolutions.skyCarePortal.dto;


import lombok.Data;

@Data
public class PasswordChangeDTO {
    private String currentPassword;
    private String newPassword;
    private String employeeID;
}
