package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.exception.TimesheetComplianceException;
import com.skycareSolutions.skyCarePortal.model.CareGiver;
import com.skycareSolutions.skyCarePortal.model.CareGiverDocumentTypes;
import com.skycareSolutions.skyCarePortal.model.CareGiverDocuments;
import com.skycareSolutions.skyCarePortal.repository.CareGiverDocumentTypesRepository;
import com.skycareSolutions.skyCarePortal.repository.CareGiverDocumentsRepository;
import com.skycareSolutions.skyCarePortal.repository.CareGiverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CareGiverDocumentComplianceService {

    public static final String TIMESHEET_BLOCKED_MESSAGE =
            "You cannot submit a timesheet due to non-compliance status. "
                    + "Please review your compliance documents and upload any missing documents.";

    private static final ZoneId AU_ZONE = ZoneId.of("Australia/Melbourne");

    @Autowired
    private GeneralSettingService generalSettingService;

    @Autowired
    private CareGiverRepository careGiverRepository;

    @Autowired
    private CareGiverDocumentTypesRepository documentTypesRepository;

    @Autowired
    private CareGiverDocumentsRepository documentsRepository;

    public void assertCanSubmitTimesheet(String employeeId) {
        if (!generalSettingService.isBlockTimesheetOnDocumentNonCompliance()) {
            return;
        }
        if (employeeId == null || employeeId.isBlank()) {
            return;
        }
        CareGiver careGiver = careGiverRepository.findByEmployeeId(employeeId);
        if (careGiver == null) {
            return;
        }
        if (!isCompliantForTimesheet(careGiver)) {
            throw new TimesheetComplianceException(TIMESHEET_BLOCKED_MESSAGE);
        }
    }

    public boolean isCompliantForTimesheet(CareGiver careGiver) {
        if (careGiver == null) {
            return true;
        }

        List<CareGiverDocumentTypes> requiredTypes = documentTypesRepository.findAll().stream()
                .filter(CareGiverDocumentTypes::isRequired)
                .filter(type -> type.getStatus() == null || "Active".equalsIgnoreCase(type.getStatus()))
                .toList();

        if (requiredTypes.isEmpty()) {
            return true;
        }

        Map<String, CareGiverDocuments> docsByType = documentsRepository.findByCareGiverID(careGiver).stream()
                .filter(doc -> doc.getId() != null && doc.getId().getDocumentTypeID() != null)
                .collect(Collectors.toMap(
                        doc -> doc.getId().getDocumentTypeID(),
                        doc -> doc,
                        (left, right) -> left
                ));

        LocalDate today = LocalDate.now(AU_ZONE);
        for (CareGiverDocumentTypes docType : requiredTypes) {
            CareGiverDocuments doc = docsByType.get(docType.getDocumentTypeID());
            if (!isDocumentCompliant(doc, docType, today)) {
                return false;
            }
        }
        return true;
    }

    private boolean isDocumentCompliant(
            CareGiverDocuments doc,
            CareGiverDocumentTypes docType,
            LocalDate today
    ) {
        boolean hasFile = doc != null && doc.getDocument() != null && !doc.getDocument().isBlank();
        if (!hasFile) {
            return false;
        }

        if (!docType.isExpDateNeeded()) {
            return true;
        }

        if (doc.getExpDate() == null) {
            return false;
        }

        LocalDate expiry = doc.getExpDate().toInstant().atZone(AU_ZONE).toLocalDate();
        return !expiry.isBefore(today);
    }
}
