package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgreementAdminSignDTO {
    private String adminEmployeeId;
    private String signatureBase64;
    private boolean reuseSavedSignature;
    private boolean saveForReuse;
    private String signatureType;
}
