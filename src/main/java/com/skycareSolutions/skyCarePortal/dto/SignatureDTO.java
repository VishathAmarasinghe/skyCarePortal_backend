package com.skycareSolutions.skyCarePortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignatureDTO {

    private Long id;
    private String employeeID;
    private String signature; // File path to the signature image
    private LocalDateTime uploadedAt;

    // Additional fields for response
    private String employeeFirstName;
    private String employeeLastName;
    private String employeeEmail;
}
