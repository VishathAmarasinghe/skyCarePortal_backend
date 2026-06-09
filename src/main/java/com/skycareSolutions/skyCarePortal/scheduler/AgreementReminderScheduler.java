package com.skycareSolutions.skyCarePortal.scheduler;

import com.skycareSolutions.skyCarePortal.model.AgreementInstance;
import com.skycareSolutions.skyCarePortal.repository.AgreementInstanceRepository;
import com.skycareSolutions.skyCarePortal.service.AgreementAuditService;
import com.skycareSolutions.skyCarePortal.service.AgreementEmailService;
import com.skycareSolutions.skyCarePortal.service.AgreementInstanceService;
import com.skycareSolutions.skyCarePortal.utils.AgreementSigningTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class AgreementReminderScheduler {

    @Autowired
    private AgreementInstanceRepository instanceRepository;

    @Autowired
    private AgreementEmailService emailService;

    @Autowired
    private AgreementAuditService auditService;

    @Value("${agreement.reminder.days-after-sent:3}")
    private int daysAfterSent;

    @Value("${agreement.reminder.max-count:3}")
    private int maxReminders;

    @Scheduled(cron = "${agreement.reminder.cron:0 0 9 * * ?}")
    @Transactional
    public void sendUnsignedReminders() {
        List<AgreementInstance> pending = instanceRepository.findPendingSignatureInstances(
                List.of(AgreementInstanceService.STATUS_SENT, AgreementInstanceService.STATUS_VIEWED),
                LocalDateTime.now()
        );
        LocalDateTime threshold = LocalDateTime.now().minusDays(daysAfterSent);

        for (AgreementInstance instance : pending) {
            int count = instance.getReminderCount() != null ? instance.getReminderCount() : 0;
            if (count >= maxReminders) {
                continue;
            }
            LocalDateTime lastSent = instance.getLastReminderAt() != null
                    ? instance.getLastReminderAt()
                    : instance.getSentAt();
            if (lastSent == null || lastSent.isAfter(threshold)) {
                continue;
            }
            try {
                String plainToken = AgreementSigningTokenUtil.generateToken();
                instance.setSigningTokenHash(AgreementSigningTokenUtil.hashToken(plainToken));
                instance.setLastReminderAt(LocalDateTime.now());
                instance.setReminderCount(count + 1);
                instance.setUpdatedAt(LocalDateTime.now());
                instanceRepository.save(instance);
                emailService.sendReminderEmail(instance, plainToken);
                auditService.log(instance.getId(), "REMINDER_SENT", "system", "SYSTEM",
                        Map.of("reminderCount", count + 1));
            } catch (Exception ignored) {
            }
        }
    }
}
