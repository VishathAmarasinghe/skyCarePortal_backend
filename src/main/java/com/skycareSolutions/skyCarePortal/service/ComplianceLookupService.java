package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.ComplianceLookupDTO;
import com.skycareSolutions.skyCarePortal.model.ComplianceLookupItem;
import com.skycareSolutions.skyCarePortal.model.ComplianceLookupType;
import com.skycareSolutions.skyCarePortal.repository.ComplianceLookupItemRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplianceLookupService {

    private static final List<String> MANDATORY_TRAINING_COURSES = Arrays.asList(
            "NDIS Orientation",
            "Behaviour Support",
            "Restrictive Practices",
            "Medication Management",
            "Manual Handling",
            "First Aid & CPR",
            "Infection Control",
            "Incident Reporting",
            "Emergency Procedures",
            "Human Rights & Dignity of Risk",
            "Fire Safety",
            "Code of Conduct"
    );

    private static final List<String> DEFAULT_AUTH_STATUSES = Arrays.asList(
            "Authorised", "Pending", "Expired", "Not Required"
    );

    private static final List<String> DEFAULT_AUTH_BODIES = Arrays.asList(
            "NDIS Commission", "Senior Practitioner", "Internal Authorisation"
    );

    private static final List<String> DEFAULT_INTENSITIES = Arrays.asList(
            "Low", "Medium", "High"
    );

    @Autowired
    private ComplianceLookupItemRepository repository;

    public List<ComplianceLookupDTO> getByType(ComplianceLookupType type) {
        ensureSeeded(type);
        return repository.findByLookupTypeOrderBySortOrderAscTitleAsc(type)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ComplianceLookupDTO getById(String id) {
        ComplianceLookupItem item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lookup item not found"));
        return toDto(item);
    }

    public ComplianceLookupDTO create(ComplianceLookupType type, ComplianceLookupDTO dto) {
        String lastId = repository.getLastLookupId();
        String newId = Micro_utils.idGenerator("complianceLookup", lastId);
        ComplianceLookupItem item = new ComplianceLookupItem();
        item.setLookupID(newId);
        item.setLookupType(type);
        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setStatus(dto.getStatus() != null ? dto.getStatus() : "Active");
        item.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        return toDto(repository.save(item));
    }

    public ComplianceLookupDTO update(String id, ComplianceLookupDTO dto) {
        ComplianceLookupItem existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lookup item not found"));
        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setStatus(dto.getStatus());
        if (dto.getSortOrder() != null) {
            existing.setSortOrder(dto.getSortOrder());
        }
        return toDto(repository.save(existing));
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    private void ensureSeeded(ComplianceLookupType type) {
        if (repository.countByLookupType(type) > 0) {
            return;
        }
        List<String> seeds = switch (type) {
            case TRAINING_COURSE -> MANDATORY_TRAINING_COURSES;
            case AUTHORISATION_STATUS -> DEFAULT_AUTH_STATUSES;
            case AUTHORISING_BODY -> DEFAULT_AUTH_BODIES;
            case BEHAVIOUR_INTENSITY -> DEFAULT_INTENSITIES;
            default -> List.of();
        };
        int order = 0;
        for (String title : seeds) {
            ComplianceLookupDTO dto = new ComplianceLookupDTO();
            dto.setTitle(title);
            dto.setStatus("Active");
            dto.setSortOrder(order++);
            create(type, dto);
        }
    }

    private ComplianceLookupDTO toDto(ComplianceLookupItem item) {
        ComplianceLookupDTO dto = new ComplianceLookupDTO();
        dto.setLookupID(item.getLookupID());
        dto.setLookupType(item.getLookupType().name());
        dto.setTitle(item.getTitle());
        dto.setDescription(item.getDescription());
        dto.setStatus(item.getStatus());
        dto.setSortOrder(item.getSortOrder());
        return dto;
    }
}
