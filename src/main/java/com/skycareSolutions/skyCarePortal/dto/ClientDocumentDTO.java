package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientDocumentDTO {
    private String clientDocumentID;
    private String documentName;
    private String documentLocation;
    private LocalDateTime createdDate;
    private String clientId;
    private String shareType;
}
