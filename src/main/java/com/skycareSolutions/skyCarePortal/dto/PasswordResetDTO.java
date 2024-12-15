package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class PasswordResetDTO {
    private String password;
    private String email;
    private String employeeID;
}
