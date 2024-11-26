package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class AppointmentAddressDTO {
    private String appointmentAddressID;
    private String address;
    private String city;
    private String state;
    private String postalCode;
}
