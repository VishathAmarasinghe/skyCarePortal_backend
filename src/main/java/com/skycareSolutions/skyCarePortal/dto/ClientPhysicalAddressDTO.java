package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class ClientPhysicalAddressDTO {
    private Long id;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String longitude;
    private String latitude;
}
