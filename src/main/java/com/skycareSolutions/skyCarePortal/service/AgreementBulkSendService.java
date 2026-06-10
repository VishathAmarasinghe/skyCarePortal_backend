package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AgreementBulkSendService {

    @Autowired
    private AgreementInstanceService instanceService;

    public AgreementBulkSendResultDTO bulkSend(AgreementBulkSendDTO dto) {
        AgreementBulkSendResultDTO result = new AgreementBulkSendResultDTO();
        String recipientType = resolveRecipientType(dto);
        List<String> recipientIds = resolveRecipientIds(dto);

        if (recipientIds.isEmpty()) {
            throw new RuntimeException("No recipients selected");
        }

        for (String recipientId : recipientIds) {
            try {
                AgreementInstanceCreateDTO create = new AgreementInstanceCreateDTO();
                create.setRecipientType(recipientType);
                create.setRecipientId(recipientId);
                create.setTemplateKey(dto.getTemplateKey());
                create.setCreatedBy(dto.getSentBy());
                AgreementInstanceDTO instance = instanceService.createInstance(create);

                AgreementAdminSignDTO sign = new AgreementAdminSignDTO();
                sign.setAdminEmployeeId(dto.getAdminEmployeeId());
                sign.setReuseSavedSignature(true);
                sign.setSaveForReuse(true);
                sign.setSignatureType("SAVED");
                instance = instanceService.adminSign(instance.getId(), sign);

                AgreementSendDTO send = new AgreementSendDTO();
                send.setEmailTemplateId(dto.getEmailTemplateId());
                send.setSubjectOverride(dto.getSubjectOverride());
                send.setBodyHtmlOverride(dto.getBodyHtmlOverride());
                send.setExpiresAt(dto.getExpiresAt());
                send.setSentBy(dto.getSentBy());
                AgreementSendResponseDTO sent = instanceService.sendInstance(instance.getId(), send);
                result.getInstances().add(sent.getInstance());
                result.setSuccessCount(result.getSuccessCount() + 1);
            } catch (Exception e) {
                result.setFailureCount(result.getFailureCount() + 1);
                result.getErrors().add(recipientId + ": " + e.getMessage());
            }
        }
        return result;
    }

    private String resolveRecipientType(AgreementBulkSendDTO dto) {
        if (dto.getRecipientType() != null && !dto.getRecipientType().isBlank()) {
            return dto.getRecipientType().trim().toUpperCase();
        }
        return "WORKER";
    }

    private List<String> resolveRecipientIds(AgreementBulkSendDTO dto) {
        if (dto.getRecipientIds() != null && !dto.getRecipientIds().isEmpty()) {
            return dto.getRecipientIds();
        }
        if (dto.getCareGiverIds() != null && !dto.getCareGiverIds().isEmpty()) {
            return dto.getCareGiverIds();
        }
        return new ArrayList<>();
    }
}
