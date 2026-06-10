package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.RestrictivePracticeEvidenceDTO;
import com.skycareSolutions.skyCarePortal.model.Client;
import com.skycareSolutions.skyCarePortal.model.RestrictivePracticeEvidence;
import com.skycareSolutions.skyCarePortal.repository.ClientRepository;
import com.skycareSolutions.skyCarePortal.repository.EmployeeRepository;
import com.skycareSolutions.skyCarePortal.repository.RestrictivePracticeEvidenceRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RestrictivePracticeEvidenceService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Autowired
    private RestrictivePracticeEvidenceRepository repository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RegisterAccessService registerAccessService;

    public List<RestrictivePracticeEvidenceDTO> list(
            String requesterRole, String requesterEmployeeId,
            String clientId, String rpType, String from, String to, String q
    ) {
        Set<String> allowedClients = registerAccessService.isCareGiverRole(requesterRole)
                ? registerAccessService.getAssignedClientIdsForEmployee(requesterEmployeeId)
                : null;
        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);
        String query = q != null ? q.trim().toLowerCase(Locale.ROOT) : "";

        return repository.findAll().stream()
                .filter(row -> allowedClients == null || allowedClients.contains(row.getClient().getClientID()))
                .filter(row -> clientId == null || clientId.isBlank() || row.getClient().getClientID().equals(clientId))
                .filter(row -> rpType == null || rpType.isBlank()
                        || rpType.equalsIgnoreCase(row.getRestrictivePracticeType()))
                .filter(row -> fromDate == null || row.getReviewDate() == null || !row.getReviewDate().isBefore(fromDate))
                .filter(row -> toDate == null || row.getReviewDate() == null || !row.getReviewDate().isAfter(toDate))
                .filter(row -> query.isEmpty() || matchesQuery(row, query))
                .sorted(Comparator.comparing(RestrictivePracticeEvidence::getReviewDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public RestrictivePracticeEvidenceDTO getById(String id, String requesterRole, String requesterEmployeeId) {
        RestrictivePracticeEvidence row = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evidence record not found"));
        assertReadAccess(row, requesterRole, requesterEmployeeId);
        return toDto(row);
    }

    public RestrictivePracticeEvidenceDTO create(RestrictivePracticeEvidenceDTO dto, String requesterRole, String requesterEmployeeId) {
        registerAccessService.assertAdmin(requesterRole);
        RestrictivePracticeEvidence row = new RestrictivePracticeEvidence();
        row.setEvidenceID(Micro_utils.idGenerator("restrictivePracticeEvidence", repository.getLastEvidenceId()));
        applyDto(row, dto);
        LocalDateTime now = LocalDateTime.now();
        row.setCreatedAt(now);
        row.setUpdatedAt(now);
        row.setCreatedBy(requesterEmployeeId);
        return toDto(repository.save(row));
    }

    public RestrictivePracticeEvidenceDTO update(String id, RestrictivePracticeEvidenceDTO dto, String requesterRole) {
        registerAccessService.assertAdmin(requesterRole);
        RestrictivePracticeEvidence row = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evidence record not found"));
        applyDto(row, dto);
        row.setUpdatedAt(LocalDateTime.now());
        return toDto(repository.save(row));
    }

    public void delete(String id, String requesterRole) {
        registerAccessService.assertAdmin(requesterRole);
        repository.deleteById(id);
    }

    private void applyDto(RestrictivePracticeEvidence row, RestrictivePracticeEvidenceDTO dto) {
        Client client = clientRepository.findById(dto.getClientID())
                .orElseThrow(() -> new RuntimeException("Client not found"));
        row.setClient(client);
        row.setRestrictivePracticeType(dto.getRestrictivePracticeType());
        row.setBaselineFrequency(dto.getBaselineFrequency());
        row.setCurrentFrequency(dto.getCurrentFrequency());
        row.setReductionGoal(dto.getReductionGoal());
        row.setStrategiesImplemented(dto.getStrategiesImplemented());
        row.setAlternativeSupportsUsed(dto.getAlternativeSupportsUsed());
        row.setStaffTrainingCompleted(dto.isStaffTrainingCompleted());
        row.setBehaviourDataReviewed(dto.isBehaviourDataReviewed());
        row.setReviewDate(parseDate(dto.getReviewDate()));
        row.setOutcomeProgress(dto.getOutcomeProgress());
        row.setNextActions(dto.getNextActions());
        if (dto.getResponsibleEmployeeID() != null && !dto.getResponsibleEmployeeID().isBlank()) {
            row.setResponsibleEmployee(employeeRepository.findById(dto.getResponsibleEmployeeID())
                    .orElseThrow(() -> new RuntimeException("Responsible person not found")));
        } else {
            row.setResponsibleEmployee(null);
        }
    }

    private void assertReadAccess(RestrictivePracticeEvidence row, String requesterRole, String requesterEmployeeId) {
        if (registerAccessService.isAdminRole(requesterRole)) return;
        Set<String> allowed = registerAccessService.getAssignedClientIdsForEmployee(requesterEmployeeId);
        if (allowed.contains(row.getClient().getClientID())) return;
        throw new RuntimeException("Access denied");
    }

    private boolean matchesQuery(RestrictivePracticeEvidence row, String query) {
        return List.of(
                row.getClient().getFirstName(), row.getClient().getLastName(),
                row.getRestrictivePracticeType(), row.getOutcomeProgress(), row.getNextActions()
        ).stream().filter(Objects::nonNull)
                .anyMatch(v -> v.toLowerCase(Locale.ROOT).contains(query));
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        return LocalDate.parse(value, DATE_FMT);
    }

    private RestrictivePracticeEvidenceDTO toDto(RestrictivePracticeEvidence row) {
        RestrictivePracticeEvidenceDTO dto = new RestrictivePracticeEvidenceDTO();
        dto.setEvidenceID(row.getEvidenceID());
        dto.setClientID(row.getClient().getClientID());
        dto.setParticipantName(row.getClient().getFirstName() + " " + row.getClient().getLastName());
        dto.setRestrictivePracticeType(row.getRestrictivePracticeType());
        dto.setBaselineFrequency(row.getBaselineFrequency());
        dto.setCurrentFrequency(row.getCurrentFrequency());
        dto.setReductionGoal(row.getReductionGoal());
        dto.setStrategiesImplemented(row.getStrategiesImplemented());
        dto.setAlternativeSupportsUsed(row.getAlternativeSupportsUsed());
        dto.setStaffTrainingCompleted(row.isStaffTrainingCompleted());
        dto.setBehaviourDataReviewed(row.isBehaviourDataReviewed());
        dto.setReviewDate(row.getReviewDate() != null ? row.getReviewDate().format(DATE_FMT) : null);
        dto.setOutcomeProgress(row.getOutcomeProgress());
        dto.setNextActions(row.getNextActions());
        if (row.getResponsibleEmployee() != null) {
            dto.setResponsibleEmployeeID(row.getResponsibleEmployee().getEmployeeID());
            dto.setResponsiblePersonName(row.getResponsibleEmployee().getFirstName() + " "
                    + row.getResponsibleEmployee().getLastName());
        }
        return dto;
    }
}
