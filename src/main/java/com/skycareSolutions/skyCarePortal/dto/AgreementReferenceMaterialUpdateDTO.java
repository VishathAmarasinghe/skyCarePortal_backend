package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgreementReferenceMaterialUpdateDTO {
    private String title;
    private String description;
    private String audience;
    private String careGiverType;
    private Integer sortOrder;
    private Boolean active;
}
