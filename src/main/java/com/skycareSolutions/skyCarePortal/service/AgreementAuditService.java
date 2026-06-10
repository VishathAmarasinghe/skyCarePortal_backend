package com.skycareSolutions.skyCarePortal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycareSolutions.skyCarePortal.model.AgreementEvent;
import com.skycareSolutions.skyCarePortal.repository.AgreementEventRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AgreementAuditService {

    @Autowired
    private AgreementEventRepository eventRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void log(String instanceId, String eventType, String actorId, String actorType, Map<String, ?> metadata) {
        AgreementEvent event = new AgreementEvent();
        event.setId(Micro_utils.idGenerator("agreementEvent", eventRepository.getLastId()));
        event.setInstanceId(instanceId);
        event.setEventType(eventType);
        event.setActorId(actorId);
        event.setActorType(actorType);
        event.setCreatedAt(LocalDateTime.now());
        if (metadata != null && !metadata.isEmpty()) {
            try {
                event.setMetadata(objectMapper.writeValueAsString(metadata));
            } catch (Exception e) {
                event.setMetadata("{}");
            }
        }
        eventRepository.save(event);
    }
}
