package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgreementRecipientSignDTO {
    private String signatureBase64;
    private String signatureType;
    private boolean consentAccepted;
    /** Optional HTML with recipient checkbox selections (☑/☐) applied before final PDF generation. */
    private String markedHtml;
}
