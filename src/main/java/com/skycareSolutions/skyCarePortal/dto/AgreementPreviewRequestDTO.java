package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgreementPreviewRequestDTO {
    private String workerId;
    private String clientId;
    private boolean useSampleData = true;
}
