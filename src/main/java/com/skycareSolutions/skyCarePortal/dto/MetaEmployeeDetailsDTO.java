package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class MetaEmployeeDetailsDTO {
    private String employeeID;
    private String firstName;
    private String lastName;
    private String email;
    private String accessRole;
    private String profile_photo;
    private String status;

}
