package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class DocumentComplianceInsightsDTO {
    private DocumentComplianceSummaryDTO summary = new DocumentComplianceSummaryDTO();
    private Map<String, Long> expiryTimelineByWeek = new LinkedHashMap<>();
    private Map<String, Integer> expiringByDocumentType = new LinkedHashMap<>();
    private List<DocumentComplianceItemDTO> attentionItems = new ArrayList<>();
    private List<DocumentComplianceItemDTO> items = new ArrayList<>();
}
