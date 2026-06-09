package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgreementStatusUpdateDTO {
    private String status;
    private String reason;
    private String actorId;
}
