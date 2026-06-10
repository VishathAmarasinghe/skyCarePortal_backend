package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgreementEventDTO {
    private String id;
    private String instanceId;
    private String eventType;
    private String actorId;
    private String actorType;
    private String metadata;
    private LocalDateTime createdAt;
}
