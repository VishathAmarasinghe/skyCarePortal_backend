package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class ClientLookupDTO {
    private String clientID;
    private String firstName;
    private String lastName;
    private String referenceNo;
    private String displayName;
}
