package com.skycareSolutions.skyCarePortal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycareSolutions.skyCarePortal.dto.StaffTrainingRecordDTO;
import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.model.Employee_job_Roles;
import com.skycareSolutions.skyCarePortal.model.StaffTrainingRecord;
import com.skycareSolutions.skyCarePortal.repository.EmployeeRepository;
import com.skycareSolutions.skyCarePortal.repository.StaffTrainingRecordRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import com.skycareSolutions.skyCarePortal.utils.StaffTrainingCertificateFileUtil;
import com.skycareSolutions.skyCarePortal.utils.TemporaryHiddenEmployees;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StaffTrainingRecordService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private StaffTrainingRecordRepository repository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RegisterAccessService registerAccessService;

    @Autowired
    private ObjectMapper objectMapper;

    public List<StaffTrainingRecordDTO> list(
            String requesterRole,
            String requesterEmployeeId,
            String employeeId,
            String course,
            String provider,
            String expiryStatus,
            String from,
            String to,
            Boolean competencyAssessed,
            Boolean certificateFiled,
            String q
    ) {
        String scopedEmployeeId = employeeId;
        if (registerAccessService.isCareGiverRole(requesterRole)) {
            scopedEmployeeId = requesterEmployeeId;
        }

        final String employeeFilter = scopedEmployeeId;
        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);
        LocalDate today = LocalDate.now();
        LocalDate expiringCutoff = today.plusDays(30);

        String query = q != null ? q.trim().toLowerCase(Locale.ROOT) : "";

        return repository.findAll().stream()
                .filter(record -> record.getEmployee() == null
                        || !TemporaryHiddenEmployees.isHiddenEmployee(record.getEmployee().getEmployeeID()))
                .filter(record -> employeeFilter == null || employeeFilter.isBlank()
                        || (record.getEmployee() != null
                        && record.getEmployee().getEmployeeID().equals(employeeFilter)))
                .filter(record -> course == null || course.isBlank()
                        || course.equalsIgnoreCase(record.getTrainingCourse()))
                .filter(record -> provider == null || provider.isBlank()
                        || provider.equalsIgnoreCase(record.getProvider()))
                .filter(record -> competencyAssessed == null
                        || record.isCompetencyAssessed() == competencyAssessed)
                .filter(record -> certificateFiled == null
                        || record.isCertificateFiled() == certificateFiled)
                .filter(record -> fromDate == null || record.getDateCompleted() == null
                        || !record.getDateCompleted().isBefore(fromDate))
                .filter(record -> toDate == null || record.getDateCompleted() == null
                        || !record.getDateCompleted().isAfter(toDate))
                .filter(record -> matchesExpiryStatus(record, expiryStatus, today, expiringCutoff))
                .filter(record -> query.isEmpty() || matchesQuery(record, query))
                .sorted(Comparator.comparing(StaffTrainingRecord::getUpdatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public StaffTrainingRecordDTO getById(String id, String requesterRole, String requesterEmployeeId) {
        StaffTrainingRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training record not found"));
        assertReadAccess(record, requesterRole, requesterEmployeeId);
        return toDto(record);
    }

    public StaffTrainingRecordDTO create(
            String recordJson,
            MultipartFile certificate,
            String requesterRole,
            String requesterEmployeeId
    ) throws Exception {
        registerAccessService.assertAdmin(requesterRole);
        StaffTrainingRecordDTO dto = objectMapper.readValue(recordJson, StaffTrainingRecordDTO.class);
        StaffTrainingRecord record = new StaffTrainingRecord();
        String lastId = repository.getLastRecordId();
        record.setRecordID(Micro_utils.idGenerator("staffTrainingRecord", lastId));
        applyDto(record, dto);
        if (certificate != null && !certificate.isEmpty()) {
            record.setCertificatePath(StaffTrainingCertificateFileUtil.storeFile(certificate));
            record.setCertificateFiled(true);
        }
        LocalDateTime now = LocalDateTime.now();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        record.setCreatedBy(requesterEmployeeId);
        return toDto(repository.save(record));
    }

    public StaffTrainingRecordDTO update(
            String id,
            String recordJson,
            MultipartFile certificate,
            String requesterRole,
            String requesterEmployeeId
    ) throws Exception {
        registerAccessService.assertAdmin(requesterRole);
        StaffTrainingRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training record not found"));
        StaffTrainingRecordDTO dto = objectMapper.readValue(recordJson, StaffTrainingRecordDTO.class);
        applyDto(record, dto);
        if (dto.isClearCertificate()) {
            deleteCertificateFile(record);
            record.setCertificatePath(null);
            record.setCertificateFiled(false);
        }
        if (certificate != null && !certificate.isEmpty()) {
            deleteCertificateFile(record);
            record.setCertificatePath(StaffTrainingCertificateFileUtil.storeFile(certificate));
            record.setCertificateFiled(true);
        }
        record.setUpdatedAt(LocalDateTime.now());
        return toDto(repository.save(record));
    }

    public void delete(String id, String requesterRole) {
        registerAccessService.assertAdmin(requesterRole);
        repository.deleteById(id);
    }

    public File getCertificateFile(String id, String requesterRole, String requesterEmployeeId) {
        StaffTrainingRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training record not found"));
        assertReadAccess(record, requesterRole, requesterEmployeeId);
        if (record.getCertificatePath() == null || record.getCertificatePath().isBlank()) {
            throw new RuntimeException("No certificate uploaded");
        }
        File file = new File(record.getCertificatePath());
        if (!file.exists()) {
            throw new RuntimeException("Certificate file not found");
        }
        return file;
    }

    private void applyDto(StaffTrainingRecord record, StaffTrainingRecordDTO dto) {
        if (dto.getEmployeeID() != null && !dto.getEmployeeID().isBlank()) {
            Employee employee = employeeRepository.findById(dto.getEmployeeID())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            record.setEmployee(employee);
            record.setStaffNameManual(null);
            if (dto.getPosition() != null && !dto.getPosition().isBlank()) {
                record.setPosition(dto.getPosition());
            } else {
                record.setPosition(resolvePosition(employee));
            }
        } else if (dto.getStaffNameManual() != null && !dto.getStaffNameManual().isBlank()) {
            record.setEmployee(null);
            record.setStaffNameManual(dto.getStaffNameManual().trim());
            record.setPosition(dto.getPosition());
        } else if (dto.getEmployeeName() != null && !dto.getEmployeeName().isBlank()) {
            record.setEmployee(null);
            record.setStaffNameManual(dto.getEmployeeName().trim());
            record.setPosition(dto.getPosition());
        } else {
            throw new RuntimeException("Staff name is required");
        }
        record.setTrainingCourse(dto.getTrainingCourse());
        record.setProvider(dto.getProvider());
        record.setDateCompleted(parseDate(dto.getDateCompleted()));
        record.setExpiryDate(parseDate(dto.getExpiryDate()));
        record.setCertificateFiled(dto.isCertificateFiled());
        record.setCompetencyAssessed(dto.isCompetencyAssessed());
        if (dto.getAssessorEmployeeID() != null && !dto.getAssessorEmployeeID().isBlank()) {
            record.setAssessorEmployee(employeeRepository.findById(dto.getAssessorEmployeeID())
                    .orElseThrow(() -> new RuntimeException("Assessor not found")));
            record.setAssessorNameManual(null);
        } else {
            String manualAssessor = dto.getAssessorNameManual();
            if ((manualAssessor == null || manualAssessor.isBlank())
                    && dto.getAssessorName() != null
                    && !dto.getAssessorName().isBlank()) {
                manualAssessor = dto.getAssessorName();
            }
            if (manualAssessor != null && !manualAssessor.isBlank()) {
                record.setAssessorEmployee(null);
                record.setAssessorNameManual(manualAssessor.trim());
            } else {
                record.setAssessorEmployee(null);
                record.setAssessorNameManual(null);
            }
        }
        record.setComments(dto.getComments());
    }

    private String resolvePosition(Employee employee) {
        if (employee.getEmployeeJobRoles() == null || employee.getEmployeeJobRoles().isEmpty()) {
            return employee.getAccessRole();
        }
        return employee.getEmployeeJobRoles().stream()
                .map(Employee_job_Roles::getId)
                .filter(id -> id != null && id.getJobRole() != null)
                .map(id -> id.getJobRole())
                .findFirst()
                .orElse(employee.getAccessRole());
    }

    private void assertReadAccess(StaffTrainingRecord record, String requesterRole, String requesterEmployeeId) {
        if (registerAccessService.isAdminRole(requesterRole)) {
            return;
        }
        if (registerAccessService.isCareGiverRole(requesterRole)
                && record.getEmployee() != null
                && record.getEmployee().getEmployeeID().equals(requesterEmployeeId)) {
            return;
        }
        throw new RuntimeException("Access denied");
    }

    private String resolveStaffName(StaffTrainingRecord record) {
        if (record.getEmployee() != null) {
            return record.getEmployee().getFirstName() + " " + record.getEmployee().getLastName();
        }
        return record.getStaffNameManual() != null ? record.getStaffNameManual() : "";
    }

    private boolean matchesExpiryStatus(
            StaffTrainingRecord record,
            String expiryStatus,
            LocalDate today,
            LocalDate expiringCutoff
    ) {
        if (expiryStatus == null || expiryStatus.isBlank() || "ALL".equalsIgnoreCase(expiryStatus)) {
            return true;
        }
        LocalDate expiry = record.getExpiryDate();
        return switch (expiryStatus.toUpperCase(Locale.ROOT)) {
            case "CURRENT" -> expiry == null || !expiry.isBefore(today);
            case "EXPIRED" -> expiry != null && expiry.isBefore(today);
            case "EXPIRING_30" -> expiry != null && !expiry.isBefore(today) && !expiry.isAfter(expiringCutoff);
            default -> true;
        };
    }

    private boolean matchesQuery(StaffTrainingRecord record, String query) {
        return Stream.of(
                        record.getEmployee() != null ? record.getEmployee().getFirstName() : null,
                        record.getEmployee() != null ? record.getEmployee().getLastName() : null,
                        record.getStaffNameManual(),
                        record.getTrainingCourse(),
                        record.getProvider(),
                        record.getPosition(),
                        record.getComments()
                )
                .filter(value -> value != null)
                .anyMatch(value -> value.toLowerCase(Locale.ROOT).contains(query));
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value, DATE_FMT);
    }

    private StaffTrainingRecordDTO toDto(StaffTrainingRecord record) {
        StaffTrainingRecordDTO dto = new StaffTrainingRecordDTO();
        dto.setRecordID(record.getRecordID());
        if (record.getEmployee() != null) {
            dto.setEmployeeID(record.getEmployee().getEmployeeID());
        }
        dto.setStaffNameManual(record.getStaffNameManual());
        dto.setEmployeeName(resolveStaffName(record));
        dto.setPosition(record.getPosition());
        dto.setTrainingCourse(record.getTrainingCourse());
        dto.setProvider(record.getProvider());
        dto.setDateCompleted(record.getDateCompleted() != null ? record.getDateCompleted().format(DATE_FMT) : null);
        dto.setExpiryDate(record.getExpiryDate() != null ? record.getExpiryDate().format(DATE_FMT) : null);
        dto.setCertificateFiled(record.isCertificateFiled());
        dto.setHasCertificate(record.getCertificatePath() != null && !record.getCertificatePath().isBlank());
        dto.setCompetencyAssessed(record.isCompetencyAssessed());
        if (record.getAssessorEmployee() != null) {
            dto.setAssessorEmployeeID(record.getAssessorEmployee().getEmployeeID());
            dto.setAssessorName(record.getAssessorEmployee().getFirstName() + " "
                    + record.getAssessorEmployee().getLastName());
        } else if (record.getAssessorNameManual() != null) {
            dto.setAssessorNameManual(record.getAssessorNameManual());
            dto.setAssessorName(record.getAssessorNameManual());
        }
        dto.setComments(record.getComments());
        dto.setCreatedAt(record.getCreatedAt() != null ? record.getCreatedAt().format(DT_FMT) : null);
        dto.setUpdatedAt(record.getUpdatedAt() != null ? record.getUpdatedAt().format(DT_FMT) : null);
        dto.setCreatedBy(record.getCreatedBy());
        dto.setExpiryStatus(resolveExpiryLabel(record.getExpiryDate()));
        return dto;
    }

    private void deleteCertificateFile(StaffTrainingRecord record) {
        if (record.getCertificatePath() == null || record.getCertificatePath().isBlank()) {
            return;
        }
        File file = new File(record.getCertificatePath());
        if (file.exists()) {
            file.delete();
        }
    }

    private String resolveExpiryLabel(LocalDate expiryDate) {
        if (expiryDate == null) {
            return "No expiry";
        }
        LocalDate today = LocalDate.now();
        if (expiryDate.isBefore(today)) {
            return "Expired";
        }
        if (!expiryDate.isAfter(today.plusDays(30))) {
            return "Expiring soon";
        }
        return "Current";
    }
}
