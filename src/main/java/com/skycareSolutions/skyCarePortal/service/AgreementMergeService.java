package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.AgreementPreviewRequestDTO;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.CareGiverRepository;
import com.skycareSolutions.skyCarePortal.repository.ClientRepository;
import com.skycareSolutions.skyCarePortal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skycareSolutions.skyCarePortal.utils.AgreementLogoUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class AgreementMergeService {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CareGiverRepository careGiverRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrganizationSettingService organizationSettingService;

    @Autowired
    private AgreementCarePlanResolver carePlanResolver;

    @Value("${app.external.service.baseUrl:http://localhost:5000}")
    private String backendBaseUrl;

    public String mergeHtml(
            String contentHtml,
            AgreementTemplate template,
            AgreementTemplateVersion version,
            AgreementPreviewRequestDTO request
    ) {
        Map<String, String> tokens = buildTokenMap(template, version, request);
        return applyTokens(contentHtml, tokens);
    }

    public String mergeForInstance(
            String contentHtml,
            AgreementTemplate template,
            AgreementTemplateVersion version,
            String recipientType,
            String recipientId,
            String adminSignatureHtml,
            String recipientSignatureHtml
    ) {
        return mergeForInstance(
                contentHtml, template, version, recipientType, recipientId,
                adminSignatureHtml, recipientSignatureHtml, null, null);
    }

    public String mergeForInstance(
            String contentHtml,
            AgreementTemplate template,
            AgreementTemplateVersion version,
            String recipientType,
            String recipientId,
            String adminSignatureHtml,
            String recipientSignatureHtml,
            LocalDateTime adminSignedAt,
            LocalDateTime recipientSignedAt
    ) {
        AgreementPreviewRequestDTO request = new AgreementPreviewRequestDTO();
        request.setUseSampleData(false);
        if ("WORKER".equalsIgnoreCase(recipientType)) {
            request.setWorkerId(recipientId);
        } else if ("CLIENT".equalsIgnoreCase(recipientType)) {
            request.setClientId(recipientId);
        }
        Map<String, String> tokens = buildTokenMap(template, version, request);
        tokens.put("{{signature.admin}}", adminSignatureHtml != null ? adminSignatureHtml : "");
        tokens.put("{{signature.recipient}}", recipientSignatureHtml != null ? recipientSignatureHtml : "");
        tokens.put("{{signature.adminDate}}", formatDateTime(adminSignedAt));
        tokens.put("{{signature.recipientDate}}", formatDateTime(recipientSignedAt));
        return applyTokens(contentHtml, tokens);
    }

    /** @deprecated use overload with recipient signature */
    public String mergeForInstance(
            String contentHtml,
            AgreementTemplate template,
            AgreementTemplateVersion version,
            String recipientType,
            String recipientId,
            String adminSignatureHtml
    ) {
        return mergeForInstance(contentHtml, template, version, recipientType, recipientId, adminSignatureHtml, "");
    }

    private String applyTokens(String contentHtml, Map<String, String> tokens) {
        String merged = contentHtml != null ? contentHtml : "";
        for (Map.Entry<String, String> entry : tokens.entrySet()) {
            merged = merged.replace(entry.getKey(), entry.getValue() != null ? entry.getValue() : "");
        }
        return merged;
    }

    private Map<String, String> buildTokenMap(
            AgreementTemplate template,
            AgreementTemplateVersion version,
            AgreementPreviewRequestDTO request
    ) {
        Map<String, String> tokens = new HashMap<>();
        if (request != null && !request.isUseSampleData()) {
            populateWorkerTokens(tokens, request.getWorkerId());
            populateClientTokens(tokens, request.getClientId());
            carePlanResolver.populateCarePlanTokens(tokens, request.getClientId(), request.getWorkerId());
        } else {
            populateSampleWorkerTokens(tokens);
            populateSampleClientTokens(tokens);
            carePlanResolver.populateSampleCarePlanTokens(tokens);
        }
        populateOrgTokens(tokens, template);
        populateAgreementTokens(tokens, template, version);
        populateDateTokens(tokens);
        populateSignaturePreviewTokens(tokens);
        return tokens;
    }

    private void populateSignaturePreviewTokens(Map<String, String> tokens) {
        tokens.put("{{signature.admin}}", "");
        tokens.put("{{signature.recipient}}", "");
        tokens.put("{{signature.adminDate}}", "");
        tokens.put("{{signature.recipientDate}}", "");
    }

    private String formatDateTime(LocalDateTime value) {
        if (value == null) {
            return "";
        }
        return value.format(DATE_FORMAT);
    }

    private void populateWorkerTokens(Map<String, String> tokens, String workerId) {
        if (workerId == null || workerId.isBlank()) {
            populateSampleWorkerTokens(tokens);
            return;
        }
        CareGiver careGiver = careGiverRepository.findById(workerId).orElse(null);
        if (careGiver == null || careGiver.getEmployee() == null) {
            populateSampleWorkerTokens(tokens);
            return;
        }
        Employee employee = employeeRepository.findById(careGiver.getEmployee().getEmployeeID()).orElse(null);
        if (employee == null) {
            populateSampleWorkerTokens(tokens);
            return;
        }
        tokens.put("{{worker.firstName}}", safe(employee.getFirstName()));
        tokens.put("{{worker.lastName}}", safe(employee.getLastName()));
        tokens.put("{{worker.fullName}}", safe(employee.getFirstName()) + " " + safe(employee.getLastName()));
        tokens.put("{{worker.email}}", safe(employee.getEmail()));
        tokens.put("{{worker.employeeId}}", safe(employee.getEmployeeID()));
        tokens.put("{{worker.careGiverId}}", safe(careGiver.getCareGiverID()));
        tokens.put("{{worker.careGiverType}}", safe(careGiver.getCareGiverType()));
        tokens.put("{{worker.status}}", safe(employee.getStatus()));
        tokens.put("{{worker.emergencyContactName}}", safe(employee.getEmergencyUser()));
        tokens.put("{{worker.emergencyContactPhone}}", safe(employee.getEmergencyPhoneNo()));
        tokens.put("{{worker.joinDate}}", employee.getJoinDate() != null ? employee.getJoinDate().toString() : "");
        tokens.put("{{worker.positionTitle}}", "Disability Support Worker");

        if (employee.getEmployeePhoneNo() != null && !employee.getEmployeePhoneNo().isEmpty()) {
            tokens.put("{{worker.phone}}", safe(employee.getEmployeePhoneNo().get(0).getId().getPhoneNo()));
        } else {
            tokens.put("{{worker.phone}}", "");
        }

        if (employee.getEmployeeAddresses() != null && !employee.getEmployeeAddresses().isEmpty()) {
            Employee_address address = employee.getEmployeeAddresses().get(0);
            tokens.put("{{worker.addressLine1}}", safe(address.getAddress()));
            tokens.put("{{worker.city}}", safe(address.getCity()));
            tokens.put("{{worker.state}}", safe(address.getState()));
            tokens.put("{{worker.postalCode}}", safe(address.getPostal_code()));
            tokens.put("{{worker.address}}", formatAddress(address));
        } else {
            tokens.put("{{worker.addressLine1}}", "");
            tokens.put("{{worker.city}}", "");
            tokens.put("{{worker.state}}", "");
            tokens.put("{{worker.postalCode}}", "");
            tokens.put("{{worker.address}}", "");
        }
    }

    private void populateSampleWorkerTokens(Map<String, String> tokens) {
        tokens.put("{{worker.firstName}}", "Yaneth");
        tokens.put("{{worker.lastName}}", "Karunarathne");
        tokens.put("{{worker.fullName}}", "Yaneth Karunarathne");
        tokens.put("{{worker.email}}", "yaneth.k@example.com");
        tokens.put("{{worker.phone}}", "+61 400 000 000");
        tokens.put("{{worker.addressLine1}}", "6, Katelyn Ct");
        tokens.put("{{worker.city}}", "Waurn Ponds");
        tokens.put("{{worker.state}}", "VIC");
        tokens.put("{{worker.postalCode}}", "3216");
        tokens.put("{{worker.address}}", "6, Katelyn Ct, Waurn Ponds, VIC 3216");
        tokens.put("{{worker.employeeId}}", "EM00001");
        tokens.put("{{worker.careGiverId}}", "CG00001");
        tokens.put("{{worker.careGiverType}}", "Contractor");
        tokens.put("{{worker.joinDate}}", "04 May 2026");
        tokens.put("{{worker.status}}", "Activated");
        tokens.put("{{worker.emergencyContactName}}", "Emergency Contact");
        tokens.put("{{worker.emergencyContactPhone}}", "+61 400 111 222");
        tokens.put("{{worker.positionTitle}}", "Disability Support Worker");
    }

    private void populateClientTokens(Map<String, String> tokens, String clientId) {
        if (clientId == null || clientId.isBlank()) {
            populateSampleClientTokens(tokens);
            return;
        }
        Client client = clientRepository.findById(clientId).orElse(null);
        if (client == null) {
            populateSampleClientTokens(tokens);
            return;
        }
        tokens.put("{{client.firstName}}", safe(client.getFirstName()));
        tokens.put("{{client.lastName}}", safe(client.getLastName()));
        tokens.put("{{client.fullName}}", safe(client.getFirstName()) + " " + safe(client.getLastName()));
        tokens.put("{{client.preferredName}}", safe(client.getPreferredName()));
        tokens.put("{{client.email}}", safe(client.getEmail()));
        tokens.put("{{client.clientId}}", safe(client.getClientID()));
        tokens.put("{{client.ndisNumber}}", safe(client.getReferenceNo()));
        tokens.put("{{client.dateOfBirth}}", client.getBirthday() != null ? client.getBirthday().toString() : "");
        tokens.put("{{client.phone}}", safe(client.getEmergencyPhoneNo()));
        tokens.put("{{client.address}}", formatClientAddress(client));
    }

    private String formatClientAddress(Client client) {
        if (client.getPhysicalAddress() == null) {
            return "";
        }
        ClientPhysicalAddress address = client.getPhysicalAddress();
        StringBuilder sb = new StringBuilder();
        if (address.getAddress() != null && !address.getAddress().isBlank()) {
            sb.append(address.getAddress().trim());
        }
        if (address.getCity() != null && !address.getCity().isBlank()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(address.getCity().trim());
        }
        if (address.getState() != null && !address.getState().isBlank()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(address.getState().trim());
        }
        if (address.getPostalCode() != null && !address.getPostalCode().isBlank()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(address.getPostalCode().trim());
        }
        return sb.toString();
    }

    private void populateSampleClientTokens(Map<String, String> tokens) {
        tokens.put("{{client.firstName}}", "Alex");
        tokens.put("{{client.lastName}}", "Johnson");
        tokens.put("{{client.fullName}}", "Alex Johnson");
        tokens.put("{{client.preferredName}}", "Alex");
        tokens.put("{{client.email}}", "alex@example.com");
        tokens.put("{{client.phone}}", "+61 400 333 444");
        tokens.put("{{client.address}}", "12 Main St, Geelong VIC 3220");
        tokens.put("{{client.clientId}}", "CU00001");
        tokens.put("{{client.ndisNumber}}", "430000000");
        tokens.put("{{client.dateOfBirth}}", "15 Jan 1990");
    }

    private void populateOrgTokens(Map<String, String> tokens, AgreementTemplate template) {
        var org = organizationSettingService.getEntity();
        tokens.put("{{org.name}}", safe(org.getName()));
        tokens.put("{{org.abn}}", safe(org.getAbn()));
        tokens.put("{{org.address}}", safe(org.getAddress()));
        tokens.put("{{org.phone}}", safe(org.getPhone()));
        tokens.put("{{org.email}}", safe(org.getEmail()));
        tokens.put("{{org.directorName}}", safe(org.getDirectorName()));
        if (AgreementLogoUtil.isClientServiceAgreementTemplate(template)) {
            tokens.put("{{org.logo}}", AgreementLogoUtil.buildServiceAgreementLogoHtml());
        } else {
            String logoUrl = organizationSettingService.buildLogoUrl(org);
            tokens.put("{{org.logo}}", logoUrl != null ? AgreementLogoUtil.buildLogoImgHtml(logoUrl) : "");
        }
    }

    private void populateAgreementTokens(
            Map<String, String> tokens,
            AgreementTemplate template,
            AgreementTemplateVersion version
    ) {
        tokens.put("{{agreement.templateName}}", safe(template.getDisplayName()));
        tokens.put("{{agreement.templateKey}}", safe(template.getTemplateKey()));
        tokens.put("{{agreement.version}}", version != null ? "v" + version.getVersionNumber() : "");
        tokens.put("{{agreement.commencementDate}}", LocalDate.now().format(DATE_FORMAT));
        tokens.put("{{agreement.sentDate}}", LocalDate.now().format(DATE_FORMAT));
    }

    private void populateDateTokens(Map<String, String> tokens) {
        tokens.put("{{today.date}}", LocalDate.now().format(DATE_FORMAT));
        tokens.put("{{today.year}}", String.valueOf(LocalDate.now().getYear()));
    }

    public String buildAssetUrl(String assetId) {
        return backendBaseUrl + "/agreement-templates/assets/" + assetId;
    }

    private String formatAddress(Employee_address address) {
        StringBuilder builder = new StringBuilder();
        appendPart(builder, address.getAddress());
        appendPart(builder, address.getCity());
        appendPart(builder, address.getState());
        appendPart(builder, address.getPostal_code());
        return builder.toString().trim();
    }

    private void appendPart(StringBuilder builder, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(value.trim());
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}
