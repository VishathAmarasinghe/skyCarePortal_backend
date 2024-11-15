package com.skycareSolutions.skyCarePortal.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class Employee_address_DTO {
    private String longitude;
    private String latitude;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postal_code;
}
