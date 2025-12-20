package com.skycareSolutions.skyCarePortal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NaplincWaitlistResponseDTO {
    private String message;
    private boolean success;
    private boolean alreadySignedUp;
}

