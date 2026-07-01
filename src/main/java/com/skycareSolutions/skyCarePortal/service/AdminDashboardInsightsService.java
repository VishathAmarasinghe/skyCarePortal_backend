package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.AgreementInstance;
import com.skycareSolutions.skyCarePortal.model.CareGiver;
import com.skycareSolutions.skyCarePortal.model.CareGiverDocumentTypes;
import com.skycareSolutions.skyCarePortal.model.CareGiverDocuments;
import com.skycareSolutions.skyCarePortal.repository.AgreementInstanceRepository;
import com.skycareSolutions.skyCarePortal.repository.CareGiverDocumentsRepository;
import com.skycareSolutions.skyCarePortal.repository.CareGiverDocumentTypesRepository;
import com.skycareSolutions.skyCarePortal.repository.CareGiverRepository;
import com.skycareSolutions.skyCarePortal.repository.ShiftNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminDashboardInsightsService {

    private static final ZoneId AU_ZONE = ZoneId.of("Australia/Melbourne");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final String ISSUE_EXPIRED = "EXPIRED";
    private static final String ISSUE_EXPIRING = "EXPIRING";
    private static final String ISSUE_MISSING_REQUIRED = "MISSING_REQUIRED";
    private static final String ISSUE_NO_EXPIRY_DATE = "NO_EXPIRY_DATE";

    @Autowired
    private ShiftNoteRepository shiftNoteRepository;

    @Autowired
    private CareGiverRepository careGiverRepository;

    @Autowired
    private CareGiverDocumentTypesRepository documentTypesRepository;

    @Autowired
    private CareGiverDocumentsRepository documentsRepository;

    @Autowired
    private AgreementInstanceRepository agreementInstanceRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ClientService clientService;

    public AdminInsightsDashboardDTO getInsights(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now(AU_ZONE);
        if (to == null) {
            to = today;
        }
        if (from == null) {
            from = to.minusDays(13);
        }
        if (from.isAfter(to)) {
            LocalDate swap = from;
            from = to;
            to = swap;
        }

        AdminInsightsDashboardDTO dto = new AdminInsightsDashboardDTO();
        dto.setStaffCount(employeeService.getEmployeesByRole("CareGiver").size());
        dto.setClientCount(clientService.getAllClients().size());
        dto.setTimesheet(buildTimesheetInsights(from, to));
        dto.setDocuments(buildDocumentCompliance(today));
        dto.setAgreements(buildAgreementAttention());
        return dto;
    }

    private TimesheetInsightsDTO buildTimesheetInsights(LocalDate from, LocalDate to) {
        TimesheetInsightsDTO insights = new TimesheetInsightsDTO();
        insights.setFromDate(from.format(DATE_FMT));
        insights.setToDate(to.format(DATE_FMT));

        long periodDays = ChronoUnit.DAYS.between(from, to) + 1;
        LocalDate previousTo = from.minusDays(1);
        LocalDate previousFrom = previousTo.minusDays(periodDays - 1);
        insights.setPreviousFromDate(previousFrom.format(DATE_FMT));
        insights.setPreviousToDate(previousTo.format(DATE_FMT));

        Map<String, Long> submissionsByDay = new LinkedHashMap<>();
        long totalReceived = 0;
        String peakDay = from.format(DATE_FMT);
        long peakDayCount = 0;

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            LocalDateTime dayStart = date.atStartOfDay(AU_ZONE).toLocalDateTime();
            LocalDateTime dayEnd = date.atTime(23, 59, 59).atZone(AU_ZONE).toLocalDateTime();
            long count = shiftNoteRepository.countByCreatedAtBetween(dayStart, dayEnd);
            String key = date.format(DATE_FMT);
            submissionsByDay.put(key, count);
            totalReceived += count;
            if (count > peakDayCount) {
                peakDayCount = count;
                peakDay = key;
            }
        }

        LocalDateTime prevStart = previousFrom.atStartOfDay(AU_ZONE).toLocalDateTime();
        LocalDateTime prevEnd = previousTo.atTime(23, 59, 59).atZone(AU_ZONE).toLocalDateTime();
        long previousPeriodTotal = shiftNoteRepository.countByCreatedAtBetween(prevStart, prevEnd);

        insights.setSubmissionsByDay(submissionsByDay);
        insights.setTotalReceived(totalReceived);
        insights.setPreviousPeriodTotal(previousPeriodTotal);
        insights.setChangePercent(calculateChangePercent(totalReceived, previousPeriodTotal));
        insights.setAveragePerDay(periodDays > 0 ? (double) totalReceived / periodDays : 0);
        insights.setPeakDay(peakDay);
        insights.setPeakDayCount(peakDayCount);
        return insights;
    }

    private Double calculateChangePercent(long current, long previous) {
        if (previous == 0) {
            return current == 0 ? 0.0 : null;
        }
        return ((current - previous) / (double) previous) * 100.0;
    }

    private DocumentComplianceInsightsDTO buildDocumentCompliance(LocalDate today) {
        DocumentComplianceInsightsDTO insights = new DocumentComplianceInsightsDTO();
        DocumentComplianceSummaryDTO summary = insights.getSummary();

        List<CareGiver> activeCareGivers = careGiverRepository.findAll().stream()
                .filter(cg -> cg.getEmployee() != null
                        && "Activated".equalsIgnoreCase(cg.getEmployee().getStatus()))
                .toList();

        List<CareGiverDocumentTypes> activeTypes = documentTypesRepository.findAll().stream()
                .filter(type -> type.getStatus() == null || "Active".equalsIgnoreCase(type.getStatus()))
                .toList();

        Map<String, Map<String, CareGiverDocuments>> docsByCareGiver = documentsRepository.findAll().stream()
                .filter(doc -> doc.getId() != null && doc.getId().getCareGiverID() != null)
                .collect(Collectors.groupingBy(
                        doc -> doc.getId().getCareGiverID(),
                        Collectors.toMap(
                                doc -> doc.getId().getDocumentTypeID(),
                                doc -> doc,
                                (left, right) -> left
                        )
                ));

        List<DocumentComplianceItemDTO> items = new ArrayList<>();
        Map<String, Long> expiryTimeline = initExpiryTimeline(today);
        Map<String, Integer> expiringByType = new LinkedHashMap<>();

        for (CareGiver careGiver : activeCareGivers) {
            Map<String, CareGiverDocuments> caregiverDocs =
                    docsByCareGiver.getOrDefault(careGiver.getCareGiverID(), Map.of());
            EmployeeDTO employee = mapEmployee(careGiver);

            for (CareGiverDocumentTypes docType : activeTypes) {
                CareGiverDocuments doc = caregiverDocs.get(docType.getDocumentTypeID());
                DocumentComplianceItemDTO item = evaluateDocument(careGiver, employee, docType, doc, today);
                if (item == null) {
                    continue;
                }
                items.add(item);
                updateSummary(summary, item);
                updateExpiryTimeline(expiryTimeline, item, today);
                updateExpiringByType(expiringByType, item);
            }
        }

        items.sort(this::compareComplianceItems);
        insights.setItems(items);
        insights.setAttentionItems(items.stream().limit(10).toList());
        insights.setExpiryTimelineByWeek(expiryTimeline);
        insights.setExpiringByDocumentType(expiringByType);
        return insights;
    }

    private EmployeeDTO mapEmployee(CareGiver careGiver) {
        EmployeeDTO employee = new EmployeeDTO();
        if (careGiver.getEmployee() != null) {
            employee.setEmployeeID(careGiver.getEmployee().getEmployeeID());
            employee.setFirstName(careGiver.getEmployee().getFirstName());
            employee.setLastName(careGiver.getEmployee().getLastName());
        }
        return employee;
    }

    private DocumentComplianceItemDTO evaluateDocument(
            CareGiver careGiver,
            EmployeeDTO employee,
            CareGiverDocumentTypes docType,
            CareGiverDocuments doc,
            LocalDate today
    ) {
        boolean hasFile = doc != null && doc.getDocument() != null && !doc.getDocument().isBlank();

        if (!hasFile) {
            if (!docType.isRequired()) {
                return null;
            }
            return buildItem(careGiver, employee, docType, doc, ISSUE_MISSING_REQUIRED, null, today);
        }

        if (docType.isExpDateNeeded()) {
            if (doc.getExpDate() == null) {
                return buildItem(careGiver, employee, docType, doc, ISSUE_NO_EXPIRY_DATE, null, today);
            }
            Integer daysUntilExpiry = calculateDaysUntilExpiry(doc.getExpDate(), today);
            if (daysUntilExpiry < 0) {
                return buildItem(careGiver, employee, docType, doc, ISSUE_EXPIRED, daysUntilExpiry, today);
            }
            if (daysUntilExpiry <= 90) {
                return buildItem(careGiver, employee, docType, doc, ISSUE_EXPIRING, daysUntilExpiry, today);
            }
            return null;
        }

        return null;
    }

    private DocumentComplianceItemDTO buildItem(
            CareGiver careGiver,
            EmployeeDTO employee,
            CareGiverDocumentTypes docType,
            CareGiverDocuments doc,
            String issueType,
            Integer daysUntilExpiry,
            LocalDate today
    ) {
        DocumentComplianceItemDTO item = new DocumentComplianceItemDTO();
        item.setEmployeeId(employee.getEmployeeID());
        item.setCareGiverId(careGiver.getCareGiverID());
        item.setEmployeeName(formatName(employee));
        item.setCareGiverType(careGiver.getCareGiverType());
        item.setDocumentTypeId(docType.getDocumentTypeID());
        item.setDocumentTypeName(docType.getDocumentName());
        item.setRequired(docType.isRequired());
        item.setIssueType(issueType);
        item.setDaysUntilExpiry(daysUntilExpiry);
        if (doc != null) {
            item.setStatus(doc.getStatus());
            if (doc.getExpDate() != null) {
                item.setExpDate(doc.getExpDate().toInstant().atZone(AU_ZONE).toLocalDate().format(DATE_FMT));
            }
        }
        return item;
    }

    private Integer calculateDaysUntilExpiry(Date expDate, LocalDate today) {
        LocalDate expiry = expDate.toInstant().atZone(AU_ZONE).toLocalDate();
        return (int) ChronoUnit.DAYS.between(today, expiry);
    }

    private String formatName(EmployeeDTO employee) {
        String first = employee.getFirstName() != null ? employee.getFirstName().trim() : "";
        String last = employee.getLastName() != null ? employee.getLastName().trim() : "";
        return (first + " " + last).trim();
    }

    private void updateSummary(DocumentComplianceSummaryDTO summary, DocumentComplianceItemDTO item) {
        switch (item.getIssueType()) {
            case ISSUE_EXPIRED -> summary.setExpired(summary.getExpired() + 1);
            case ISSUE_EXPIRING -> {
                Integer days = item.getDaysUntilExpiry();
                if (days != null) {
                    if (days >= 0 && days <= 7) {
                        summary.setExpiringIn7Days(summary.getExpiringIn7Days() + 1);
                    }
                    if (days >= 0 && days <= 30) {
                        summary.setExpiringIn30Days(summary.getExpiringIn30Days() + 1);
                    }
                }
            }
            case ISSUE_MISSING_REQUIRED -> summary.setMissingRequired(summary.getMissingRequired() + 1);
            case ISSUE_NO_EXPIRY_DATE -> summary.setMissingExpiryDate(summary.getMissingExpiryDate() + 1);
            default -> {
            }
        }
    }

    private Map<String, Long> initExpiryTimeline(LocalDate today) {
        Map<String, Long> timeline = new LinkedHashMap<>();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        for (int week = 0; week < 13; week++) {
            LocalDate bucketStart = weekStart.plusWeeks(week);
            timeline.put(bucketStart.format(DATE_FMT), 0L);
        }
        return timeline;
    }

    private void updateExpiryTimeline(
            Map<String, Long> timeline,
            DocumentComplianceItemDTO item,
            LocalDate today
    ) {
        if (!ISSUE_EXPIRING.equals(item.getIssueType()) || item.getDaysUntilExpiry() == null) {
            return;
        }
        LocalDate expiryDate = today.plusDays(item.getDaysUntilExpiry());
        if (item.getDaysUntilExpiry() > 90) {
            return;
        }
        LocalDate bucket = expiryDate.with(DayOfWeek.MONDAY);
        String key = bucket.format(DATE_FMT);
        if (timeline.containsKey(key)) {
            timeline.put(key, timeline.get(key) + 1);
        }
    }

    private void updateExpiringByType(Map<String, Integer> expiringByType, DocumentComplianceItemDTO item) {
        if (!ISSUE_EXPIRING.equals(item.getIssueType()) || item.getDaysUntilExpiry() == null) {
            return;
        }
        if (item.getDaysUntilExpiry() < 0 || item.getDaysUntilExpiry() > 30) {
            return;
        }
        String typeName = item.getDocumentTypeName() != null ? item.getDocumentTypeName() : "Unknown";
        expiringByType.merge(typeName, 1, Integer::sum);
    }

    private int compareComplianceItems(DocumentComplianceItemDTO a, DocumentComplianceItemDTO b) {
        int urgencyA = urgencyScore(a);
        int urgencyB = urgencyScore(b);
        if (urgencyA != urgencyB) {
            return Integer.compare(urgencyA, urgencyB);
        }
        if (a.getDaysUntilExpiry() != null && b.getDaysUntilExpiry() != null
                && !a.getDaysUntilExpiry().equals(b.getDaysUntilExpiry())) {
            return Integer.compare(a.getDaysUntilExpiry(), b.getDaysUntilExpiry());
        }
        String nameA = a.getEmployeeName() != null ? a.getEmployeeName() : "";
        String nameB = b.getEmployeeName() != null ? b.getEmployeeName() : "";
        int nameCompare = nameA.compareToIgnoreCase(nameB);
        if (nameCompare != 0) {
            return nameCompare;
        }
        String docA = a.getDocumentTypeName() != null ? a.getDocumentTypeName() : "";
        String docB = b.getDocumentTypeName() != null ? b.getDocumentTypeName() : "";
        return docA.compareToIgnoreCase(docB);
    }

    private int urgencyScore(DocumentComplianceItemDTO item) {
        return switch (item.getIssueType()) {
            case ISSUE_EXPIRED -> 0;
            case ISSUE_EXPIRING -> item.getDaysUntilExpiry() != null && item.getDaysUntilExpiry() <= 7 ? 1 : 5;
            case ISSUE_MISSING_REQUIRED -> 2;
            case ISSUE_NO_EXPIRY_DATE -> 3;
            default -> 6;
        };
    }

    private AgreementAttentionDTO buildAgreementAttention() {
        AgreementAttentionDTO attention = new AgreementAttentionDTO();
        LocalDateTime now = LocalDateTime.now(AU_ZONE);

        List<AgreementInstance> instances = agreementInstanceRepository.findAll();
        int awaiting = 0;
        int sent = 0;
        int viewed = 0;
        int expired = 0;

        for (AgreementInstance instance : instances) {
            String status = instance.getStatus();
            if (AgreementInstanceService.STATUS_CANCELLED.equals(status)
                    || AgreementInstanceService.STATUS_SIGNED.equals(status)
                    || AgreementInstanceService.STATUS_DRAFT.equals(status)) {
                continue;
            }

            boolean isExpired = AgreementInstanceService.STATUS_EXPIRED.equals(status)
                    || (instance.getExpiresAt() != null && instance.getExpiresAt().isBefore(now));

            if (isExpired) {
                expired++;
                continue;
            }

            if (AgreementInstanceService.STATUS_SENT.equals(status)) {
                sent++;
                awaiting++;
            } else if (AgreementInstanceService.STATUS_VIEWED.equals(status)) {
                viewed++;
                awaiting++;
            } else if (AgreementInstanceService.STATUS_ADMIN_SIGNED.equals(status)) {
                awaiting++;
            }
        }

        attention.setAwaitingSignature(awaiting);
        attention.setSent(sent);
        attention.setViewed(viewed);
        attention.setExpired(expired);
        return attention;
    }
}
