package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.RestrictivePracticeRegisterDTO;
import com.skycareSolutions.skyCarePortal.model.Client;
import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.model.RestrictivePracticeRegister;
import com.skycareSolutions.skyCarePortal.repository.ClientRepository;
import com.skycareSolutions.skyCarePortal.repository.EmployeeRepository;
import com.skycareSolutions.skyCarePortal.repository.RestrictivePracticeRegisterRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import com.skycareSolutions.skyCarePortal.utils.TemporaryHiddenClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RestrictivePracticeRegisterService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Autowired
    private RestrictivePracticeRegisterRepository repository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RegisterAccessService registerAccessService;

    public List<RestrictivePracticeRegisterDTO> list(
            String requesterRole, String requesterEmployeeId,
            String clientId, String ndisNumber, String rpType, String authStatus, String q
    ) {
        Set<String> allowedClients = registerAccessService.isCareGiverRole(requesterRole)
                ? registerAccessService.getAssignedClientIdsForEmployee(requesterEmployeeId)
                : null;
        String query = q != null ? q.trim().toLowerCase(Locale.ROOT) : "";

        return repository.findAll().stream()
                .filter(row -> row.getClient() == null || !TemporaryHiddenClients.isHidden(row.getClient().getClientID()))
                .filter(row -> allowedClients == null || allowedClients.contains(row.getClient().getClientID()))
                .filter(row -> clientId == null || clientId.isBlank() || row.getClient().getClientID().equals(clientId))
                .filter(row -> ndisNumber == null || ndisNumber.isBlank()
                        || (row.getNdisNumber() != null && row.getNdisNumber().contains(ndisNumber)))
                .filter(row -> rpType == null || rpType.isBlank()
                        || rpType.equalsIgnoreCase(row.getRestrictivePracticeType()))
                .filter(row -> authStatus == null || authStatus.isBlank()
                        || authStatus.equalsIgnoreCase(row.getAuthorisationStatus()))
                .filter(row -> query.isEmpty() || matchesQuery(row, query))
                .sorted(Comparator.comparing(RestrictivePracticeRegister::getUpdatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public RestrictivePracticeRegisterDTO getById(String id, String requesterRole, String requesterEmployeeId) {
        RestrictivePracticeRegister row = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Register entry not found"));
        if (row.getClient() != null && TemporaryHiddenClients.isHidden(row.getClient().getClientID())) {
            throw new RuntimeException("Register entry not found");
        }
        assertReadAccess(row, requesterRole, requesterEmployeeId);
        return toDto(row);
    }

    public RestrictivePracticeRegisterDTO create(RestrictivePracticeRegisterDTO dto, String requesterRole, String requesterEmployeeId) {
        registerAccessService.assertAdmin(requesterRole);
        validateRequiredFields(dto);
        RestrictivePracticeRegister row = new RestrictivePracticeRegister();
        row.setRegisterID(Micro_utils.idGenerator("restrictivePracticeRegister", repository.getLastRegisterId()));
        applyDto(row, dto);
        LocalDateTime now = LocalDateTime.now();
        row.setCreatedAt(now);
        row.setUpdatedAt(now);
        row.setCreatedBy(requesterEmployeeId);
        return toDto(repository.save(row));
    }

    public RestrictivePracticeRegisterDTO update(String id, RestrictivePracticeRegisterDTO dto, String requesterRole) {
        registerAccessService.assertAdmin(requesterRole);
        validateRequiredFields(dto);
        RestrictivePracticeRegister row = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Register entry not found"));
        applyDto(row, dto);
        row.setUpdatedAt(LocalDateTime.now());
        return toDto(repository.save(row));
    }

    public void delete(String id, String requesterRole) {
        registerAccessService.assertAdmin(requesterRole);
        repository.deleteById(id);
    }

    private void validateRequiredFields(RestrictivePracticeRegisterDTO dto) {
        if (dto.getClientID() == null || dto.getClientID().isBlank()) {
            throw new RuntimeException("Participant is required");
        }
    }

    private void applyDto(RestrictivePracticeRegister row, RestrictivePracticeRegisterDTO dto) {
        Client client = clientRepository.findById(dto.getClientID())
                .orElseThrow(() -> new RuntimeException("Client not found"));
        row.setClient(client);
        row.setNdisNumber(dto.getNdisNumber() != null && !dto.getNdisNumber().isBlank()
                ? dto.getNdisNumber() : client.getReferenceNo());
        row.setRestrictivePracticeType(dto.getRestrictivePracticeType());
        row.setDescription(dto.getDescription());
        row.setBehaviourSupportPlanCurrent(dto.isBehaviourSupportPlanCurrent());
        row.setAuthorisationStatus(dto.getAuthorisationStatus());
        row.setAuthorisingBody(dto.getAuthorisingBody());
        row.setDateAuthorised(parseDate(dto.getDateAuthorised()));
        row.setReviewDate(parseDate(dto.getReviewDate()));
        if (dto.getPractitionerEmployeeID() != null && !dto.getPractitionerEmployeeID().isBlank()) {
            row.setPractitionerEmployee(employeeRepository.findById(dto.getPractitionerEmployeeID())
                    .orElseThrow(() -> new RuntimeException("Practitioner not found")));
        } else {
            row.setPractitionerEmployee(null);
        }
        row.setReductionStrategyInPlace(dto.isReductionStrategyInPlace());
        row.setIncidentIds(joinIncidentIds(dto.getIncidentIds()));
        row.setStaffTrained(dto.isStaffTrained());
        row.setComments(dto.getComments());
    }

    private void assertReadAccess(RestrictivePracticeRegister row, String requesterRole, String requesterEmployeeId) {
        if (registerAccessService.isAdminRole(requesterRole)) return;
        Set<String> allowed = registerAccessService.getAssignedClientIdsForEmployee(requesterEmployeeId);
        if (allowed.contains(row.getClient().getClientID())) return;
        throw new RuntimeException("Access denied");
    }

    private boolean matchesQuery(RestrictivePracticeRegister row, String query) {
        return List.of(
                row.getClient().getFirstName(), row.getClient().getLastName(),
                row.getNdisNumber(), row.getRestrictivePracticeType(),
                row.getDescription(), row.getComments()
        ).stream().filter(Objects::nonNull)
                .anyMatch(v -> v.toLowerCase(Locale.ROOT).contains(query));
    }

    private String joinIncidentIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return null;
        return String.join(",", ids);
    }

    private List<String> splitIncidentIds(String value) {
        if (value == null || value.isBlank()) return List.of();
        return Arrays.stream(value.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        return LocalDate.parse(value, DATE_FMT);
    }

    private RestrictivePracticeRegisterDTO toDto(RestrictivePracticeRegister row) {
        RestrictivePracticeRegisterDTO dto = new RestrictivePracticeRegisterDTO();
        dto.setRegisterID(row.getRegisterID());
        dto.setClientID(row.getClient().getClientID());
        dto.setParticipantName(row.getClient().getFirstName() + " " + row.getClient().getLastName());
        dto.setNdisNumber(row.getNdisNumber());
        dto.setRestrictivePracticeType(row.getRestrictivePracticeType());
        dto.setDescription(row.getDescription());
        dto.setBehaviourSupportPlanCurrent(row.isBehaviourSupportPlanCurrent());
        dto.setAuthorisationStatus(row.getAuthorisationStatus());
        dto.setAuthorisingBody(row.getAuthorisingBody());
        dto.setDateAuthorised(row.getDateAuthorised() != null ? row.getDateAuthorised().format(DATE_FMT) : null);
        dto.setReviewDate(row.getReviewDate() != null ? row.getReviewDate().format(DATE_FMT) : null);
        if (row.getPractitionerEmployee() != null) {
            dto.setPractitionerEmployeeID(row.getPractitionerEmployee().getEmployeeID());
            dto.setPractitionerName(row.getPractitionerEmployee().getFirstName() + " "
                    + row.getPractitionerEmployee().getLastName());
        }
        dto.setReductionStrategyInPlace(row.isReductionStrategyInPlace());
        dto.setIncidentIds(splitIncidentIds(row.getIncidentIds()));
        dto.setStaffTrained(row.isStaffTrained());
        dto.setComments(row.getComments());
        return dto;
    }
}
