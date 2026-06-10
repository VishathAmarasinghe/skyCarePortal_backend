package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.AgreementTemplateAssignmentDTO;
import com.skycareSolutions.skyCarePortal.model.AgreementTemplate;
import com.skycareSolutions.skyCarePortal.model.AgreementTemplateAssignment;
import com.skycareSolutions.skyCarePortal.repository.AgreementTemplateAssignmentRepository;
import com.skycareSolutions.skyCarePortal.repository.AgreementTemplateRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgreementAssignmentService {

    @Autowired
    private AgreementTemplateAssignmentRepository assignmentRepository;

    @Autowired
    private AgreementTemplateRepository templateRepository;

    public List<AgreementTemplateAssignmentDTO> getAllAssignments() {
        return assignmentRepository.findAllByOrderByPriorityAsc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AgreementTemplateAssignmentDTO> replaceAssignments(List<AgreementTemplateAssignmentDTO> assignments) {
        assignmentRepository.deleteAll();
        int priority = 1;
        for (AgreementTemplateAssignmentDTO input : assignments) {
            AgreementTemplate template = templateRepository.findById(input.getTemplateId())
                    .orElseThrow(() -> new RuntimeException("Template not found: " + input.getTemplateId()));
            AgreementTemplateAssignment assignment = new AgreementTemplateAssignment();
            assignment.setId(Micro_utils.idGenerator("agreementTemplateAssignment", assignmentRepository.getLastId()));
            assignment.setTemplate(template);
            assignment.setRuleType(input.getRuleType() != null ? input.getRuleType() : "MATCH_CARE_GIVER_TYPE");
            assignment.setCareGiverType(input.getCareGiverType());
            assignment.setPriority(input.getPriority() > 0 ? input.getPriority() : priority);
            assignmentRepository.save(assignment);
            priority++;
        }
        return getAllAssignments();
    }

    private AgreementTemplateAssignmentDTO toDto(AgreementTemplateAssignment entity) {
        AgreementTemplateAssignmentDTO dto = new AgreementTemplateAssignmentDTO();
        dto.setId(entity.getId());
        dto.setTemplateId(entity.getTemplate().getId());
        dto.setTemplateKey(entity.getTemplate().getTemplateKey());
        dto.setTemplateDisplayName(entity.getTemplate().getDisplayName());
        dto.setRuleType(entity.getRuleType());
        dto.setCareGiverType(entity.getCareGiverType());
        dto.setPriority(entity.getPriority());
        return dto;
    }
}
