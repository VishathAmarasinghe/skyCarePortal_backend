package com.skycareSolutions.skyCarePortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HomeMessageSettingDTO {
    private boolean enabled;
    private String message;
    private String type; // WARNING | IMPORTANT | GENERAL
    private String updatedAt; // ISO string
}

