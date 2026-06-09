package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgreementSigningLinkDTO {
    private String signingUrl;
    private LocalDateTime expiresAt;
    private AgreementInstanceDTO instance;
    private String message;
}
