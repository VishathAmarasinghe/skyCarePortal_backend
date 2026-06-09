package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.model.AgreementTemplate;
import com.skycareSolutions.skyCarePortal.model.AgreementTemplateVersion;
import com.skycareSolutions.skyCarePortal.model.EmailTemplates;
import com.skycareSolutions.skyCarePortal.repository.AgreementTemplateRepository;
import com.skycareSolutions.skyCarePortal.repository.AgreementTemplateVersionRepository;
import com.skycareSolutions.skyCarePortal.repository.EmailRepository;
import com.skycareSolutions.skyCarePortal.utils.HtmlSanitizerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AgreementTemplateSeedRunner implements ApplicationRunner {

    @Autowired
    private AgreementTemplateRepository templateRepository;

    @Autowired
    private AgreementTemplateVersionRepository versionRepository;

    @Autowired
    private AgreementAssignmentService assignmentService;

    @Autowired
    private com.skycareSolutions.skyCarePortal.repository.AgreementTemplateAssignmentRepository assignmentRepository;

    @Autowired
    private EmailRepository emailRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedTemplatesIfMissing();
        removeUnspecifiedWorkerTemplate();
        refreshDefaultWorkerTemplates();
        seedEmailTemplatesIfMissing();
    }

    private void seedTemplatesIfMissing() {
        seedWorkerTemplate(
                "worker_contractor",
                "Contractor Service Agreement",
                "Contractor",
                AgreementSeedContent.contractorAgreementHtml(),
                AgreementSeedContent.SEED_CHANGE_NOTES_CONTRACTOR
        );
        seedWorkerTemplate(
                "worker_casual",
                "Casual Service Agreement",
                "Casual",
                AgreementSeedContent.casualAgreementHtml(),
                AgreementSeedContent.SEED_CHANGE_NOTES_CASUAL
        );
        seedWorkerTemplate(
                "worker_full_time",
                "Full-time Service Agreement",
                "Full-time",
                AgreementSeedContent.workerAgreementHtml("Full-time", "full-time"),
                "Seeded from contracts forms.txt"
        );
        seedWorkerTemplate(
                "worker_part_time",
                "Part-time Service Agreement",
                "Part-time",
                AgreementSeedContent.workerAgreementHtml("Part-time", "part-time"),
                "Seeded from contracts forms.txt"
        );
        seedClientTemplate();
        seedAssignmentsIfMissing();
    }

    private void removeUnspecifiedWorkerTemplate() {
        templateRepository.findByTemplateKey(AgreementSeedContent.RETIRED_WORKER_TEMPLATE_KEY)
                .ifPresent(template -> {
                    assignmentRepository.deleteByTemplate_Id(template.getId());
                    templateRepository.delete(template);
                });
        assignmentRepository.findAllByOrderByPriorityAsc().stream()
                .filter(a -> "Not specified".equalsIgnoreCase(a.getCareGiverType()))
                .forEach(assignmentRepository::delete);
    }

    private void refreshDefaultWorkerTemplates() {
        refreshPublishedSeedContent(
                "worker_contractor",
                AgreementSeedContent.contractorAgreementHtml(),
                AgreementSeedContent.SEED_CHANGE_NOTES_CONTRACTOR
        );
        refreshPublishedSeedContent(
                "worker_casual",
                AgreementSeedContent.casualAgreementHtml(),
                AgreementSeedContent.SEED_CHANGE_NOTES_CASUAL
        );
        refreshPublishedSeedContent(
                "client_service_agreement",
                AgreementSeedContent.clientServiceAgreementHtml(),
                AgreementSeedContent.SEED_CHANGE_NOTES_CLIENT
        );
    }

    private void refreshPublishedSeedContent(String templateKey, String html, String changeNotes) {
        templateRepository.findByTemplateKey(templateKey).ifPresent(template -> {
            versionRepository.findByTemplate_IdAndStatus(template.getId(), AgreementTemplateService.STATUS_PUBLISHED)
                    .ifPresent(version -> {
                        if (AgreementSeedContent.needsSeedContentRefresh(
                                templateKey, version.getChangeNotes(), version.getContentHtml())) {
                            version.setContentHtml(HtmlSanitizerUtil.sanitizeAgreementHtml(html));
                            version.setChangeNotes(changeNotes);
                            versionRepository.save(version);
                        }
                    });
        });
    }

    private void seedWorkerTemplate(
            String templateKey,
            String displayName,
            String careGiverType,
            String contentHtml,
            String changeNotes
    ) {
        if (templateRepository.findByTemplateKey(templateKey).isPresent()) {
            return;
        }
        AgreementTemplate template = new AgreementTemplate();
        template.setId(nextTemplateId());
        template.setTemplateKey(templateKey);
        template.setDisplayName(displayName);
        template.setAudience("WORKER");
        template.setCareGiverType(careGiverType);
        template.setDefaultForAudience(false);
        template.setStatus("Active");
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        template = templateRepository.save(template);

        AgreementTemplateVersion version = new AgreementTemplateVersion();
        version.setId(nextVersionId());
        version.setTemplate(template);
        version.setVersionNumber(1);
        version.setStatus(AgreementTemplateService.STATUS_PUBLISHED);
        version.setContentHtml(HtmlSanitizerUtil.sanitizeAgreementHtml(contentHtml));
        version.setChangeNotes(changeNotes);
        version.setPublishedAt(LocalDateTime.now());
        version.setPublishedBy("system");
        version.setCreatedAt(LocalDateTime.now());
        versionRepository.save(version);
    }

    private void seedClientTemplate() {
        if (templateRepository.findByTemplateKey("client_service_agreement").isPresent()) {
            return;
        }
        AgreementTemplate template = new AgreementTemplate();
        template.setId(nextTemplateId());
        template.setTemplateKey("client_service_agreement");
        template.setDisplayName("Client Service Agreement");
        template.setAudience("CLIENT");
        template.setCareGiverType(null);
        template.setDefaultForAudience(true);
        template.setStatus("Active");
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        template = templateRepository.save(template);

        AgreementTemplateVersion version = new AgreementTemplateVersion();
        version.setId(nextVersionId());
        version.setTemplate(template);
        version.setVersionNumber(1);
        version.setStatus(AgreementTemplateService.STATUS_PUBLISHED);
        version.setContentHtml(HtmlSanitizerUtil.sanitizeAgreementHtml(AgreementSeedContent.clientServiceAgreementHtml()));
        version.setChangeNotes(AgreementSeedContent.SEED_CHANGE_NOTES_CLIENT);
        version.setPublishedAt(LocalDateTime.now());
        version.setPublishedBy("system");
        version.setCreatedAt(LocalDateTime.now());
        versionRepository.save(version);
    }

    private void seedAssignmentsIfMissing() {
        if (!assignmentService.getAllAssignments().isEmpty()) {
            return;
        }
        List<AgreementTemplate> workerTemplates = templateRepository.findByAudience("WORKER");
        java.util.ArrayList<com.skycareSolutions.skyCarePortal.dto.AgreementTemplateAssignmentDTO> dtos =
                new java.util.ArrayList<>();
        int priority = 1;
        for (AgreementTemplate template : workerTemplates) {
            if (template.getCareGiverType() == null
                    || "Not specified".equalsIgnoreCase(template.getCareGiverType())
                    || AgreementSeedContent.RETIRED_WORKER_TEMPLATE_KEY.equals(template.getTemplateKey())) {
                continue;
            }
            com.skycareSolutions.skyCarePortal.dto.AgreementTemplateAssignmentDTO dto =
                    new com.skycareSolutions.skyCarePortal.dto.AgreementTemplateAssignmentDTO();
            dto.setTemplateId(template.getId());
            dto.setRuleType("MATCH_CARE_GIVER_TYPE");
            dto.setCareGiverType(template.getCareGiverType());
            dto.setPriority(priority++);
            dtos.add(dto);
        }
        if (!dtos.isEmpty()) {
            assignmentService.replaceAssignments(dtos);
        }
    }

    private void seedEmailTemplatesIfMissing() {
        seedEmailTemplate("ET00007", "Agreement Invitation", "AGREEMENT_INVITE",
                "<p>Dear [Recipient Name],</p><p>Please review and sign your agreement: [Agreement Title].</p><p><a href=\"[Signing URL]\">Sign agreement</a></p><p>Regards,<br/>[Organization Name]</p>",
                "[Recipient Name],[Agreement Title],[Signing URL],[Organization Name]");
        seedEmailTemplate("ET00008", "Agreement Signed Confirmation", "AGREEMENT_SIGNED_CONFIRM",
                "<p>Dear [Recipient Name],</p><p>Thank you for signing [Agreement Title].</p>",
                "[Recipient Name],[Agreement Title]");
        seedEmailTemplate("ET00009", "Agreement Admin Notification", "AGREEMENT_ADMIN_NOTIFY",
                "<p>[Recipient Name] has signed [Agreement Title].</p>",
                "[Recipient Name],[Agreement Title]");
        seedEmailTemplate("ET00010", "Agreement Reminder", "AGREEMENT_REMINDER",
                "<p>Dear [Recipient Name],</p><p>Reminder to sign [Agreement Title] before [Expiry Date].</p><p><a href=\"[Signing URL]\">Sign agreement</a></p>",
                "[Recipient Name],[Agreement Title],[Signing URL],[Expiry Date]");
    }

    private void seedEmailTemplate(String id, String title, String category, String content, String placeholders) {
        if (emailRepository.findById(id).isPresent()) {
            return;
        }
        EmailTemplates template = new EmailTemplates();
        template.setTemplateID(id);
        template.setTitle(title);
        template.setContent(content);
        template.setCategory(category);
        template.setPlaceholdersMetadata(placeholders);
        emailRepository.save(template);
    }

    private String nextTemplateId() {
        return com.skycareSolutions.skyCarePortal.utils.Micro_utils.idGenerator(
                "agreementTemplate", templateRepository.getLastId());
    }

    private String nextVersionId() {
        return com.skycareSolutions.skyCarePortal.utils.Micro_utils.idGenerator(
                "agreementTemplateVersion", versionRepository.getLastId());
    }
}
