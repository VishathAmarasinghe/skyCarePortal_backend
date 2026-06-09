package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class AgreementAttentionDTO {
    private int awaitingSignature;
    private int viewed;
    private int sent;
    private int expired;
}
