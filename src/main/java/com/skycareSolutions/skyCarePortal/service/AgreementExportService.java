package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.AgreementEvent;
import com.skycareSolutions.skyCarePortal.model.AgreementInstance;
import com.skycareSolutions.skyCarePortal.repository.AgreementEventRepository;
import com.skycareSolutions.skyCarePortal.repository.AgreementInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AgreementExportService {

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private AgreementEventRepository eventRepository;

    @Autowired
    private AgreementInstanceRepository instanceRepository;

    public byte[] exportInstanceEventsCsv(String instanceId) {
        List<AgreementEvent> events = eventRepository.findByInstanceIdOrderByCreatedAtDesc(instanceId);
        StringBuilder csv = new StringBuilder("Event Type,Actor ID,Actor Type,Created At,Metadata\n");
        for (AgreementEvent event : events) {
            csv.append(escape(event.getEventType())).append(',')
                    .append(escape(event.getActorId())).append(',')
                    .append(escape(event.getActorType())).append(',')
                    .append(event.getCreatedAt() != null ? event.getCreatedAt().format(DT) : "").append(',')
                    .append(escape(event.getMetadata())).append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportRecipientHistoryCsv(String recipientType, String recipientId) {
        List<AgreementInstance> instances =
                instanceRepository.findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(recipientType, recipientId);
        StringBuilder csv = new StringBuilder(
                "Instance ID,Template,Version,Status,Sent At,Signed At,Recipient Email\n");
        for (AgreementInstance instance : instances) {
            csv.append(escape(instance.getId())).append(',')
                    .append(escape(instance.getTemplateKey())).append(',')
                    .append(instance.getTemplateVersion() != null
                            ? instance.getTemplateVersion().getVersionNumber() : "").append(',')
                    .append(escape(instance.getStatus())).append(',')
                    .append(instance.getSentAt() != null ? instance.getSentAt().format(DT) : "").append(',')
                    .append(instance.getSignedAt() != null ? instance.getSignedAt().format(DT) : "").append(',')
                    .append(escape(instance.getRecipientEmail())).append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
