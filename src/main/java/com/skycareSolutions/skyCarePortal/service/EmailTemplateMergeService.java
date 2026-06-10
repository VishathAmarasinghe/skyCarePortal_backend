package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.EmailPlaceholderItemDTO;
import com.skycareSolutions.skyCarePortal.dto.EmailTemplatePreviewRequestDTO;
import com.skycareSolutions.skyCarePortal.dto.EmailTemplatePreviewResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class EmailTemplateMergeService {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private static final String FROM_ADDRESS = "no-reply@skycaresolutions.com.au";

    @Autowired
    private EmailTemplateLayoutService layoutService;

    @Autowired
    private OrganizationSettingService organizationSettingService;

    public List<EmailPlaceholderItemDTO> getPlaceholderReference() {
        return List.of(
                item("[Recipient Name]", "Worker or client name", "Jane Smith"),
                item("[Agreement Title]", "Agreement template display name", "Contractor Service Agreement"),
                item("[Signing URL]", "Public signing link", "https://skycare.au/sign/sample-token"),
                item("[Expiry Date]", "Link expiry date", LocalDate.now().plusDays(14).format(DATE_FORMAT)),
                item("[Organization Name]", "Organization name", sampleOrgName()),
                item("[Sender Name]", "Admin who sent the agreement", "Admin User")
        );
    }

    public EmailTemplatePreviewResponseDTO preview(EmailTemplatePreviewRequestDTO request) {
        Map<String, String> tokens = buildSampleTokens();
        if (request.getTokenOverrides() != null) {
            request.getTokenOverrides().forEach((key, value) -> {
                if (key != null && value != null) {
                    tokens.put(key, value);
                }
            });
        }
        String subject = mergeText(request.getSubject(), tokens);
        String bodyHtml = mergeText(request.getBodyHtml(), tokens);
        String fullHtml = layoutService.wrapBody(bodyHtml);

        EmailTemplatePreviewResponseDTO response = new EmailTemplatePreviewResponseDTO();
        response.setMergedSubject(subject);
        response.setMergedBodyHtml(bodyHtml);
        response.setFullHtml(fullHtml);
        response.setFromAddress(FROM_ADDRESS);
        response.setSampleRecipient(formatSampleRecipient(request, tokens));
        return response;
    }

    private String formatSampleRecipient(EmailTemplatePreviewRequestDTO request, Map<String, String> tokens) {
        String name = request.getSampleRecipientName();
        if (name == null || name.isBlank()) {
            name = tokens.getOrDefault("[Recipient Name]", "Jane Smith");
        }
        String email = request.getSampleRecipientEmail();
        if (email == null || email.isBlank()) {
            return name;
        }
        return name + " <" + email + ">";
    }

    public String buildFinalHtml(String subject, String bodyHtml, Map<String, String> tokens) {
        String mergedBody = mergeText(bodyHtml, tokens != null ? tokens : buildSampleTokens());
        return layoutService.wrapBody(mergedBody);
    }

    public String mergeSubject(String subject, Map<String, String> tokens) {
        return mergeText(subject, tokens != null ? tokens : buildSampleTokens());
    }

    private Map<String, String> buildSampleTokens() {
        Map<String, String> tokens = new LinkedHashMap<>();
        tokens.put("[Recipient Name]", "Jane Smith");
        tokens.put("[Agreement Title]", "Contractor Service Agreement");
        tokens.put("[Signing URL]", "https://skycare.au/sign/sample-token");
        tokens.put("[Expiry Date]", LocalDate.now().plusDays(14).format(DATE_FORMAT));
        tokens.put("[Organization Name]", sampleOrgName());
        tokens.put("[Sender Name]", "Admin User");
        return tokens;
    }

    private String sampleOrgName() {
        var org = organizationSettingService.getEntity();
        return org.getName() != null && !org.getName().isBlank()
                ? org.getName()
                : "SkyCare Solutions Pty Ltd";
    }

    private String mergeText(String input, Map<String, String> tokens) {
        if (input == null) {
            return "";
        }
        String merged = input;
        for (Map.Entry<String, String> entry : tokens.entrySet()) {
            merged = merged.replace(entry.getKey(), entry.getValue() != null ? entry.getValue() : "");
        }
        return merged;
    }

    private EmailPlaceholderItemDTO item(String token, String description, String example) {
        EmailPlaceholderItemDTO dto = new EmailPlaceholderItemDTO();
        dto.setToken(token);
        dto.setDescription(description);
        dto.setExampleOutput(example);
        return dto;
    }
}
