package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class NdisSupportPlanPdfRequestDTO {
    private CompanyDTO company;
    private CarePlanDTO carePlan;
    private ClientNameDTO client;
    private String generatedAtIso;

    @Data
    public static class CompanyDTO {
        private String name;
        private String logoDataUrl;
    }

    @Data
    public static class ClientNameDTO {
        private String firstName;
        private String lastName;
        private String preferredName;
    }
}

