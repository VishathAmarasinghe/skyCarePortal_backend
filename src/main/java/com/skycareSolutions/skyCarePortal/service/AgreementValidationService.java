package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.AgreementValidationResultDTO;
import com.skycareSolutions.skyCarePortal.model.AgreementInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AgreementValidationService {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{[^}]+\\}\\}");

    @Autowired
    private AgreementInstanceService instanceService;

    public AgreementValidationResultDTO validateInstance(String instanceId) {
        AgreementInstance instance = instanceService.getInstanceEntity(instanceId);
        String html = instanceService.buildMergedHtml(instance);
        return validateHtml(html, instance.getAdminSignatureId() == null);
    }

    public AgreementValidationResultDTO validateHtml(String html, boolean adminSignatureMissing) {
        AgreementValidationResultDTO result = new AgreementValidationResultDTO();
        List<String> unresolved = findUnresolvedPlaceholders(html);
        List<String> warnings = new ArrayList<>();

        if (!unresolved.isEmpty()) {
            warnings.add("Unresolved placeholders: " + String.join(", ", unresolved));
        }
        if (adminSignatureMissing && html.contains("{{signature.admin}}")) {
            warnings.add("Admin signature is required but not yet applied.");
        }
        if (html.contains("{{signature.recipient}}")) {
            warnings.add("Recipient signature placeholder will be filled when the recipient signs.");
        }

        result.setValid(
                unresolved.isEmpty() && !(adminSignatureMissing && html.contains("{{signature.admin}}")));
        result.setUnresolvedPlaceholders(unresolved);
        result.setWarnings(warnings);
        return result;
    }

    private List<String> findUnresolvedPlaceholders(String html) {
        Set<String> found = new LinkedHashSet<>();
        if (html == null) {
            return List.of();
        }
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(html);
        while (matcher.find()) {
            String token = matcher.group();
            if (!token.startsWith("{{signature.")) {
                found.add(token);
            }
        }
        return new ArrayList<>(found);
    }
}
