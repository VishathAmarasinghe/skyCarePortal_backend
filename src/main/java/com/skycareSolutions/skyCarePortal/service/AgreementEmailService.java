package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.AgreementSendDTO;
import com.skycareSolutions.skyCarePortal.model.AgreementInstance;
import com.skycareSolutions.skyCarePortal.model.EmailTemplates;
import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.repository.EmailRepository;
import com.skycareSolutions.skyCarePortal.repository.EmployeeRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AgreementEmailService {

    private static final String FROM_ADDRESS = "no-reply@skycaresolutions.com.au";
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailTemplateMergeService emailTemplateMergeService;

    @Autowired
    private OrganizationSettingService organizationSettingService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Value("${app.external.service.frontendUrl:https://skycare.au/}")
    private String frontendUrl;

    public void sendInviteEmail(AgreementInstance instance, String plainToken, AgreementSendDTO dto) {
        EmailTemplates template = resolveEmailTemplate(dto.getEmailTemplateId(), "AGREEMENT_INVITE");
        Map<String, String> tokens = buildTokens(instance, plainToken, dto.getSentBy());
        String subject = dto.getSubjectOverride() != null && !dto.getSubjectOverride().isBlank()
                ? emailTemplateMergeService.mergeSubject(dto.getSubjectOverride(), tokens)
                : emailTemplateMergeService.mergeSubject(template.getTitle(), tokens);
        String bodySource = dto.getBodyHtmlOverride() != null && !dto.getBodyHtmlOverride().isBlank()
                ? dto.getBodyHtmlOverride()
                : template.getContent();
        String html = emailTemplateMergeService.buildFinalHtml(subject, bodySource, tokens);
        send(instance.getRecipientEmail(), dto.getCcEmails(), subject, html);
    }

    public void sendSignedConfirmation(AgreementInstance instance) {
        EmailTemplates template = resolveEmailTemplate(null, "AGREEMENT_SIGNED_CONFIRM");
        Map<String, String> tokens = buildTokens(instance, null, instance.getSentBy());
        String subject = emailTemplateMergeService.mergeSubject(template.getTitle(), tokens);
        String html = emailTemplateMergeService.buildFinalHtml(subject, template.getContent(), tokens);
        send(instance.getRecipientEmail(), null, subject, html);
    }

    public void sendAdminNotification(AgreementInstance instance) {
        EmailTemplates template = resolveEmailTemplate(null, "AGREEMENT_ADMIN_NOTIFY");
        Map<String, String> tokens = buildTokens(instance, null, instance.getSentBy());
        String subject = emailTemplateMergeService.mergeSubject(template.getTitle(), tokens);
        String html = emailTemplateMergeService.buildFinalHtml(subject, template.getContent(), tokens);
        String adminEmail = resolveAdminEmail(instance.getSentBy());
        if (adminEmail != null && !adminEmail.isBlank()) {
            send(adminEmail, null, subject, html);
        }
    }

    public void sendReminderEmail(AgreementInstance instance, String plainToken) {
        EmailTemplates template = resolveEmailTemplate(null, "AGREEMENT_REMINDER");
        Map<String, String> tokens = buildTokens(instance, plainToken, instance.getSentBy());
        String subject = emailTemplateMergeService.mergeSubject(template.getTitle(), tokens);
        String html = emailTemplateMergeService.buildFinalHtml(subject, template.getContent(), tokens);
        send(instance.getRecipientEmail(), null, subject, html);
    }

    public void sendResendInvite(AgreementInstance instance, String plainToken) {
        AgreementSendDTO dto = new AgreementSendDTO();
        dto.setEmailTemplateId(instance.getEmailTemplateId());
        dto.setSentBy(instance.getSentBy());
        dto.setCcEmails(parseCcSnapshot(instance.getEmailCcSnapshot()));
        if (instance.getEmailSubjectSnapshot() != null) {
            dto.setSubjectOverride(instance.getEmailSubjectSnapshot());
        }
        if (instance.getEmailBodySnapshot() != null) {
            dto.setBodyHtmlOverride(instance.getEmailBodySnapshot());
        }
        sendInviteEmail(instance, plainToken, dto);
    }

    private EmailTemplates resolveEmailTemplate(String templateId, String category) {
        if (templateId != null && !templateId.isBlank()) {
            return emailRepository.findById(templateId)
                    .orElseThrow(() -> new RuntimeException("Email template not found"));
        }
        return emailRepository.findByCategoryStartingWithOrderByTemplateIDAsc("AGREEMENT_").stream()
                .filter(t -> category.equals(t.getCategory()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Email template not found for category: " + category));
    }

    private Map<String, String> buildTokens(AgreementInstance instance, String plainToken, String senderId) {
        Map<String, String> tokens = new LinkedHashMap<>();
        tokens.put("[Recipient Name]", instance.getRecipientName() != null ? instance.getRecipientName() : "");
        tokens.put("[Agreement Title]",
                instance.getTemplateVersion().getTemplate().getDisplayName());
        tokens.put("[Signing URL]", buildSigningUrl(plainToken));
        tokens.put("[Expiry Date]",
                instance.getExpiresAt() != null
                        ? instance.getExpiresAt().toLocalDate().format(DATE_FORMAT)
                        : LocalDate.now().plusDays(14).format(DATE_FORMAT));
        var org = organizationSettingService.getEntity();
        tokens.put("[Organization Name]",
                org.getName() != null && !org.getName().isBlank() ? org.getName() : "SkyCare Solutions Pty Ltd");
        tokens.put("[Sender Name]", resolveSenderName(senderId));
        return tokens;
    }

    private String buildSigningUrl(String plainToken) {
        String base = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;
        return base + "/sign/" + plainToken;
    }

    private String resolveSenderName(String employeeId) {
        if (employeeId == null) {
            return "SkyCare Admin";
        }
        return employeeRepository.findById(employeeId)
                .map(emp -> emp.getFirstName() + " " + emp.getLastName())
                .orElse("SkyCare Admin");
    }

    private String resolveAdminEmail(String employeeId) {
        if (employeeId == null) {
            return null;
        }
        return employeeRepository.findById(employeeId)
                .map(Employee::getEmail)
                .orElse(null);
    }

    private void send(String to, List<String> ccEmails, String subject, String html) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(FROM_ADDRESS);
            helper.setTo(to);
            List<String> ccList = normalizeCcEmails(ccEmails);
            if (!ccList.isEmpty()) {
                helper.setCc(ccList.toArray(new String[0]));
            }
            helper.setSubject(subject);
            helper.setText(html, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Unable to send agreement email", e);
        }
    }

    private List<String> normalizeCcEmails(List<String> ccEmails) {
        if (ccEmails == null || ccEmails.isEmpty()) {
            return List.of();
        }
        List<String> normalized = new ArrayList<>();
        for (String email : ccEmails) {
            if (email == null || email.isBlank()) {
                continue;
            }
            for (String part : email.split("[,;]")) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty() && !normalized.contains(trimmed)) {
                    normalized.add(trimmed);
                }
            }
        }
        return normalized;
    }

    private List<String> parseCcSnapshot(String snapshot) {
        if (snapshot == null || snapshot.isBlank()) {
            return List.of();
        }
        List<String> emails = new ArrayList<>();
        for (String part : snapshot.split("[,;]")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                emails.add(trimmed);
            }
        }
        return emails;
    }
}
