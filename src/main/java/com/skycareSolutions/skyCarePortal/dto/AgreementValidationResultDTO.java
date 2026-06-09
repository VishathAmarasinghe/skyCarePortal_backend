package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AgreementValidationResultDTO {
    private boolean valid;
    private List<String> unresolvedPlaceholders;
    private List<String> warnings;
}
