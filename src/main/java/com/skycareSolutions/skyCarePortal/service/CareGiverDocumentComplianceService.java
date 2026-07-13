package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.exception.TimesheetComplianceException;
import com.skycareSolutions.skyCarePortal.model.CareGiver;
import com.skycareSolutions.skyCarePortal.model.CareGiverDocumentTypes;
import com.skycareSolutions.skyCarePortal.model.CareGiverDocuments;
import com.skycareSolutions.skyCarePortal.repository.CareGiverDocumentTypesRepository;
import com.skycareSolutions.skyCarePortal.repository.CareGiverDocumentsRepository;
import com.skycareSolutions.skyCarePortal.repository.CareGiverRepository;
import com.skycareSolutions.skyCarePortal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CareGiverDocumentComplianceService {

    public static final String TIMESHEET_BLOCKED_MESSAGE =
            "You cannot submit a timesheet due to non-compliance status. "
                    + "Please review your compliance documents and upload any missing documents.";

    public static final String TIMESHEET_ADMIN_DISABLED_MESSAGE =
            "Your timesheet submission has been disabled by an administrator. "
                    + "Please contact your administrator for assistance.";

    private static final ZoneId AU_ZONE = ZoneId.of("Australia/Melbourne");

    @Autowired
    private GeneralSettingService generalSettingService;

    @Autowired
    private CareGiverRepository careGiverRepository;

    @Autowired
    private CareGiverDocumentTypesRepository documentTypesRepository;

    @Autowired
    private CareGiverDocumentsRepository documentsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public void assertCanSubmitTimesheet(String employeeId) {
        assertCanSubmitTimesheet(employeeId, null);
    }

    public void assertCanSubmitTimesheet(String employeeId, String careGiverId) {
        if (employeeId != null && !employeeId.isBlank()) {
            employeeRepository.findById(employeeId).ifPresent(employee -> {
                if (employee.isTimesheetSubmissionDisabled()) {
                    throw new TimesheetComplianceException(TIMESHEET_ADMIN_DISABLED_MESSAGE);
                }
            });
        } else if (careGiverId != null && !careGiverId.isBlank()) {
            CareGiver careGiver = careGiverRepository.findById(careGiverId).orElse(null);
            if (careGiver != null
                    && careGiver.getEmployee() != null
                    && careGiver.getEmployee().isTimesheetSubmissionDisabled()) {
                throw new TimesheetComplianceException(TIMESHEET_ADMIN_DISABLED_MESSAGE);
            }
        }

        if (!generalSettingService.isBlockTimesheetOnDocumentNonCompliance()) {
            return;
        }

        CareGiver careGiver = resolveCareGiver(employeeId, careGiverId);
        if (careGiver == null) {
            return;
        }

        List<String> blockingDocuments = findBlockingDocuments(careGiver);
        if (!blockingDocuments.isEmpty()) {
            String detail = String.join(", ", blockingDocuments);
            throw new TimesheetComplianceException(
                    TIMESHEET_BLOCKED_MESSAGE + " Affected document(s): " + detail + "."
            );
        }
    }

    public boolean isCompliantForTimesheet(CareGiver careGiver) {
        return careGiver != null && findBlockingDocuments(careGiver).isEmpty();
    }

    private CareGiver resolveCareGiver(String employeeId, String careGiverId) {
        if (employeeId != null && !employeeId.isBlank()) {
            CareGiver fromEmployee = careGiverRepository.findByEmployeeId(employeeId);
            if (fromEmployee != null) {
                return fromEmployee;
            }
        }
        if (careGiverId != null && !careGiverId.isBlank()) {
            return careGiverRepository.findById(careGiverId).orElse(null);
        }
        return null;
    }

    private List<String> findBlockingDocuments(CareGiver careGiver) {
        List<CareGiverDocumentTypes> requiredTypes = documentTypesRepository.findAll().stream()
                .filter(CareGiverDocumentTypes::isRequired)
                .filter(this::isActiveDocumentType)
                .toList();

        if (requiredTypes.isEmpty()) {
            return List.of();
        }

        Map<String, CareGiverDocuments> docsByType = documentsRepository
                .findByCareGiverId(careGiver.getCareGiverID())
                .stream()
                .filter(doc -> doc.getId() != null && doc.getId().getDocumentTypeID() != null)
                .collect(Collectors.toMap(
                        doc -> normalizeTypeId(doc.getId().getDocumentTypeID()),
                        doc -> doc,
                        (left, right) -> hasUploadedFile(left) ? left : right
                ));

        LocalDate today = LocalDate.now(AU_ZONE);
        List<String> blockingDocuments = new ArrayList<>();

        for (CareGiverDocumentTypes docType : requiredTypes) {
            CareGiverDocuments doc = docsByType.get(normalizeTypeId(docType.getDocumentTypeID()));
            if (!isDocumentCompliant(doc, docType, today)) {
                blockingDocuments.add(docType.getDocumentName());
            }
        }

        return blockingDocuments;
    }

    private boolean isActiveDocumentType(CareGiverDocumentTypes docType) {
        return docType.getStatus() != null
                && "Active".equalsIgnoreCase(docType.getStatus().trim());
    }

    private String normalizeTypeId(String documentTypeId) {
        return documentTypeId == null ? "" : documentTypeId.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isDocumentCompliant(
            CareGiverDocuments doc,
            CareGiverDocumentTypes docType,
            LocalDate today
    ) {
        if (!hasUploadedFile(doc)) {
            return false;
        }

        if (!docType.isExpDateNeeded()) {
            return true;
        }

        if (doc.getExpDate() == null) {
            return false;
        }

        LocalDate expiry = toLocalDate(doc.getExpDate());
        return expiry != null && !expiry.isBefore(today);
    }

    private boolean hasUploadedFile(CareGiverDocuments doc) {
        return doc != null && doc.getDocument() != null && !doc.getDocument().isBlank();
    }

    private LocalDate toLocalDate(Date date) {
        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return date.toInstant().atZone(AU_ZONE).toLocalDate();
    }
}
