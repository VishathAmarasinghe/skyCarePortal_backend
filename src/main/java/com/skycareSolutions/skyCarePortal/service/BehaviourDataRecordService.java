package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.BehaviourDataRecordDTO;
import com.skycareSolutions.skyCarePortal.model.BehaviourDataRecord;
import com.skycareSolutions.skyCarePortal.model.Client;
import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.repository.BehaviourDataRecordRepository;
import com.skycareSolutions.skyCarePortal.repository.ClientRepository;
import com.skycareSolutions.skyCarePortal.repository.EmployeeRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import com.skycareSolutions.skyCarePortal.utils.TemporaryHiddenClients;
import com.skycareSolutions.skyCarePortal.utils.TemporaryHiddenEmployees;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BehaviourDataRecordService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private BehaviourDataRecordRepository repository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RegisterAccessService registerAccessService;

    public List<BehaviourDataRecordDTO> list(
            String requesterRole, String requesterEmployeeId,
            String clientId, String from, String to, String intensity,
            Boolean followUpRequired, String q
    ) {
        Set<String> allowedClients = registerAccessService.isCareGiverRole(requesterRole)
                ? registerAccessService.getAssignedClientIdsForEmployee(requesterEmployeeId)
                : null;
        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);
        String query = q != null ? q.trim().toLowerCase(Locale.ROOT) : "";

        return repository.findAll().stream()
                .filter(row -> row.getClient() == null || !TemporaryHiddenClients.isHidden(row.getClient().getClientID()))
                .filter(row -> row.getStaffEmployee() == null
                        || !TemporaryHiddenEmployees.isHiddenEmployee(row.getStaffEmployee().getEmployeeID()))
                .filter(row -> allowedClients == null || allowedClients.contains(row.getClient().getClientID()))
                .filter(row -> clientId == null || clientId.isBlank() || row.getClient().getClientID().equals(clientId))
                .filter(row -> fromDate == null || row.getRecordDate() == null || !row.getRecordDate().isBefore(fromDate))
                .filter(row -> toDate == null || row.getRecordDate() == null || !row.getRecordDate().isAfter(toDate))
                .filter(row -> intensity == null || intensity.isBlank() || intensity.equalsIgnoreCase(row.getIntensity()))
                .filter(row -> followUpRequired == null || row.isFollowUpRequired() == followUpRequired)
                .filter(row -> query.isEmpty() || matchesQuery(row, query))
                .sorted(Comparator.comparing(BehaviourDataRecord::getRecordDate, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(BehaviourDataRecord::getRecordTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BehaviourDataRecordDTO getById(String id, String requesterRole, String requesterEmployeeId) {
        BehaviourDataRecord row = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Behaviour data record not found"));
        if (row.getClient() != null && TemporaryHiddenClients.isHidden(row.getClient().getClientID())) {
            throw new RuntimeException("Behaviour data record not found");
        }
        assertReadAccess(row, requesterRole, requesterEmployeeId);
        return toDto(row);
    }

    public BehaviourDataRecordDTO create(BehaviourDataRecordDTO dto, String requesterRole, String requesterEmployeeId) {
        validateRequiredFields(dto);
        registerAccessService.assertClientAccess(requesterRole, requesterEmployeeId, dto.getClientID());
        if (registerAccessService.isCareGiverRole(requesterRole)) {
            dto.setStaffEmployeeID(requesterEmployeeId);
        }
        BehaviourDataRecord row = new BehaviourDataRecord();
        row.setRecordID(Micro_utils.idGenerator("behaviourDataRecord", repository.getLastRecordId()));
        applyDto(row, dto);
        LocalDateTime now = LocalDateTime.now();
        row.setCreatedAt(now);
        row.setUpdatedAt(now);
        row.setCreatedBy(requesterEmployeeId);
        return toDto(repository.save(row));
    }

    public BehaviourDataRecordDTO update(String id, BehaviourDataRecordDTO dto, String requesterRole, String requesterEmployeeId) {
        validateRequiredFields(dto);
        BehaviourDataRecord row = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Behaviour data record not found"));
        if (registerAccessService.isAdminRole(requesterRole)) {
            applyDto(row, dto);
        } else if (registerAccessService.isCareGiverRole(requesterRole)
                && row.getStaffEmployee() != null
                && row.getStaffEmployee().getEmployeeID().equals(requesterEmployeeId)) {
            registerAccessService.assertClientAccess(requesterRole, requesterEmployeeId, dto.getClientID());
            dto.setStaffEmployeeID(requesterEmployeeId);
            applyDto(row, dto);
        } else {
            throw new RuntimeException("Access denied");
        }
        row.setUpdatedAt(LocalDateTime.now());
        return toDto(repository.save(row));
    }

    public void delete(String id, String requesterRole) {
        registerAccessService.assertAdmin(requesterRole);
        repository.deleteById(id);
    }

    private void validateRequiredFields(BehaviourDataRecordDTO dto) {
        if (dto.getClientID() == null || dto.getClientID().isBlank()) {
            throw new RuntimeException("Participant is required");
        }
        if (dto.getRecordDate() == null || dto.getRecordDate().isBlank()) {
            throw new RuntimeException("Date is required");
        }
        if (dto.getRecordTime() == null || dto.getRecordTime().isBlank()) {
            throw new RuntimeException("Time is required");
        }
        if (dto.getBehaviourObserved() == null || dto.getBehaviourObserved().isBlank()) {
            throw new RuntimeException("Behaviour observed is required");
        }
    }

    private void applyDto(BehaviourDataRecord row, BehaviourDataRecordDTO dto) {
        Client client = clientRepository.findById(dto.getClientID())
                .orElseThrow(() -> new RuntimeException("Client not found"));
        row.setClient(client);
        row.setRecordDate(parseDate(dto.getRecordDate()));
        row.setRecordTime(parseTime(dto.getRecordTime()));
        row.setBehaviourObserved(dto.getBehaviourObserved());
        row.setAntecedent(dto.getAntecedent());
        row.setStaffResponse(dto.getStaffResponse());
        row.setRestrictivePracticeUsed(dto.getRestrictivePracticeUsed());
        row.setOutcome(dto.getOutcome());
        row.setDuration(dto.getDuration());
        row.setIntensity(dto.getIntensity());
        row.setFollowUpRequired(dto.isFollowUpRequired());
        if (dto.getStaffEmployeeID() != null && !dto.getStaffEmployeeID().isBlank()) {
            row.setStaffEmployee(employeeRepository.findById(dto.getStaffEmployeeID())
                    .orElseThrow(() -> new RuntimeException("Staff not found")));
        } else {
            row.setStaffEmployee(null);
        }
    }

    private void assertReadAccess(BehaviourDataRecord row, String requesterRole, String requesterEmployeeId) {
        if (registerAccessService.isAdminRole(requesterRole)) return;
        Set<String> allowed = registerAccessService.getAssignedClientIdsForEmployee(requesterEmployeeId);
        if (allowed.contains(row.getClient().getClientID())) return;
        throw new RuntimeException("Access denied");
    }

    private boolean matchesQuery(BehaviourDataRecord row, String query) {
        return List.of(
                row.getClient().getFirstName(), row.getClient().getLastName(),
                row.getBehaviourObserved(), row.getAntecedent(), row.getOutcome()
        ).stream().filter(Objects::nonNull)
                .anyMatch(v -> v.toLowerCase(Locale.ROOT).contains(query));
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        return LocalDate.parse(value, DATE_FMT);
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) return null;
        return LocalTime.parse(value, TIME_FMT);
    }

    private String initials(Employee employee) {
        String first = employee.getFirstName() != null && !employee.getFirstName().isEmpty()
                ? employee.getFirstName().substring(0, 1) : "";
        String last = employee.getLastName() != null && !employee.getLastName().isEmpty()
                ? employee.getLastName().substring(0, 1) : "";
        return (first + last).toUpperCase(Locale.ROOT);
    }

    private BehaviourDataRecordDTO toDto(BehaviourDataRecord row) {
        BehaviourDataRecordDTO dto = new BehaviourDataRecordDTO();
        dto.setRecordID(row.getRecordID());
        dto.setClientID(row.getClient().getClientID());
        dto.setParticipantName(row.getClient().getFirstName() + " " + row.getClient().getLastName());
        dto.setRecordDate(row.getRecordDate() != null ? row.getRecordDate().format(DATE_FMT) : null);
        dto.setRecordTime(row.getRecordTime() != null ? row.getRecordTime().format(TIME_FMT) : null);
        dto.setBehaviourObserved(row.getBehaviourObserved());
        dto.setAntecedent(row.getAntecedent());
        dto.setStaffResponse(row.getStaffResponse());
        dto.setRestrictivePracticeUsed(row.getRestrictivePracticeUsed());
        dto.setOutcome(row.getOutcome());
        dto.setDuration(row.getDuration());
        dto.setIntensity(row.getIntensity());
        dto.setFollowUpRequired(row.isFollowUpRequired());
        if (row.getStaffEmployee() != null) {
            dto.setStaffEmployeeID(row.getStaffEmployee().getEmployeeID());
            dto.setStaffName(row.getStaffEmployee().getFirstName() + " " + row.getStaffEmployee().getLastName());
            dto.setStaffInitials(initials(row.getStaffEmployee()));
        }
        return dto;
    }
}
