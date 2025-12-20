package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class NaplincWaitlistRequestDTO {
    private String email;
    private String name;
    private String signupMethod; // "GOOGLE" or "EMAIL"
}

