package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgreementSendResponseDTO {
    private AgreementInstanceDTO instance;
    private LocalDateTime expiresAt;
    private String message;
    private String signingUrl;
}
