package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.EmailTemplateDTO;
import com.skycareSolutions.skyCarePortal.dto.EmailTemplatePreviewRequestDTO;
import com.skycareSolutions.skyCarePortal.dto.EmailTemplatePreviewResponseDTO;
import com.skycareSolutions.skyCarePortal.dto.EmailTemplateTestSendDTO;
import com.skycareSolutions.skyCarePortal.model.EmailTemplates;
import com.skycareSolutions.skyCarePortal.repository.EmailRepository;
import com.skycareSolutions.skyCarePortal.utils.HtmlSanitizerUtil;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailTemplateAdminService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailTemplateMergeService emailTemplateMergeService;

    public List<EmailTemplateDTO> getAll() {
        return getAgreementTemplates();
    }

    public List<EmailTemplateDTO> getAgreementTemplates() {
        return emailRepository.findByCategoryStartingWithOrderByTemplateIDAsc("AGREEMENT_")
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public EmailTemplateDTO getById(String id) {
        EmailTemplates entity = emailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Email template not found"));
        return toDto(entity);
    }

    public EmailTemplateDTO create(EmailTemplateDTO dto) {
        String id = Micro_utils.idGenerator("emailTemplate", getLastId());
        EmailTemplates entity = new EmailTemplates();
        entity.setTemplateID(id);
        entity.setTitle(dto.getTitle());
        entity.setContent(HtmlSanitizerUtil.sanitizeAgreementHtml(dto.getContent()));
        entity.setCategory(dto.getCategory());
        entity.setPlaceholdersMetadata(dto.getPlaceholdersMetadata());
        return toDto(emailRepository.save(entity));
    }

    public EmailTemplateDTO update(String id, EmailTemplateDTO dto) {
        EmailTemplates entity = emailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Email template not found"));
        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            entity.setContent(HtmlSanitizerUtil.sanitizeAgreementHtml(dto.getContent()));
        }
        if (dto.getCategory() != null) {
            entity.setCategory(dto.getCategory());
        }
        if (dto.getPlaceholdersMetadata() != null) {
            entity.setPlaceholdersMetadata(dto.getPlaceholdersMetadata());
        }
        return toDto(emailRepository.save(entity));
    }

    public void sendTestEmail(EmailTemplateTestSendDTO dto) {
        try {
            EmailTemplatePreviewRequestDTO previewRequest = new EmailTemplatePreviewRequestDTO();
            previewRequest.setSubject(dto.getSubject());
            previewRequest.setBodyHtml(dto.getContent());
            EmailTemplatePreviewResponseDTO preview = emailTemplateMergeService.preview(previewRequest);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(FROM_ADDRESS);
            helper.setTo(dto.getRecipientEmail());
            helper.setSubject(preview.getMergedSubject());
            helper.setText(preview.getFullHtml(), true);
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Unable to send test email", e);
        }
    }

    public EmailTemplatePreviewResponseDTO preview(EmailTemplatePreviewRequestDTO dto) {
        return emailTemplateMergeService.preview(dto);
    }

    private static final String FROM_ADDRESS = "no-reply@skycaresolutions.com.au";

    private String getLastId() {
        return emailRepository.findAll().stream()
                .map(EmailTemplates::getTemplateID)
                .max(String::compareToIgnoreCase)
                .orElse(null);
    }

    private EmailTemplateDTO toDto(EmailTemplates entity) {
        EmailTemplateDTO dto = new EmailTemplateDTO();
        dto.setTemplateID(entity.getTemplateID());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setCategory(entity.getCategory());
        dto.setPlaceholdersMetadata(entity.getPlaceholdersMetadata());
        return dto;
    }
}
