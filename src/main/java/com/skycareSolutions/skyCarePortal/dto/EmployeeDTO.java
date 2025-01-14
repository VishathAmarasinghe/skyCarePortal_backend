package com.skycareSolutions.skyCarePortal.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.skycareSolutions.skyCarePortal.model.Employee_address;
import com.skycareSolutions.skyCarePortal.model.Employee_phoneNo;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class EmployeeDTO {

    private String employeeID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String accessRole;
    private Date joinDate;
    private String profile_photo;
    private String emergencyPhoneNo;
    private String emergencyUser;
    private String status;
    private List<Employee_address_DTO> employeeAddresses;
    private List<String> employeeJobRoles;
    private List<String> employeePhoneNo;

}
