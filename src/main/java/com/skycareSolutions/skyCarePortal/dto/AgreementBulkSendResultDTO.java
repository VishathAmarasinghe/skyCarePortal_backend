package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AgreementBulkSendResultDTO {
    private int successCount;
    private int failureCount;
    private List<String> errors = new ArrayList<>();
    private List<AgreementInstanceDTO> instances = new ArrayList<>();
}
