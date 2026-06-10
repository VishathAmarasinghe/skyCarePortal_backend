package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.BehaviourDataRecordDTO;
import com.skycareSolutions.skyCarePortal.dto.RestrictivePracticeEvidenceDTO;
import com.skycareSolutions.skyCarePortal.dto.RestrictivePracticeRegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BehaviourSupportExportService {

    @Autowired
    private RestrictivePracticeRegisterService rpRegisterService;
    @Autowired
    private BehaviourDataRecordService behaviourDataService;
    @Autowired
    private RestrictivePracticeEvidenceService evidenceService;

    public byte[] exportRpRegisterCsv(
            String requesterRole, String requesterEmployeeId,
            String clientId, String ndisNumber, String rpType, String authStatus, String q
    ) {
        List<RestrictivePracticeRegisterDTO> rows = rpRegisterService.list(
                requesterRole, requesterEmployeeId, clientId, ndisNumber, rpType, authStatus, q
        );
        StringBuilder csv = new StringBuilder(
                "Participant Name,NDIS Number,Restrictive Practice Type,Description,BSP Current,Authorisation Status,Authorising Body,Date Authorised,Review Date,Practitioner,Reduction Strategy,Incident Reports,Staff Trained,Comments\n"
        );
        for (RestrictivePracticeRegisterDTO row : rows) {
            csv.append(escape(row.getParticipantName())).append(',')
                    .append(escape(row.getNdisNumber())).append(',')
                    .append(escape(row.getRestrictivePracticeType())).append(',')
                    .append(escape(row.getDescription())).append(',')
                    .append(row.isBehaviourSupportPlanCurrent() ? "Yes" : "No").append(',')
                    .append(escape(row.getAuthorisationStatus())).append(',')
                    .append(escape(row.getAuthorisingBody())).append(',')
                    .append(escape(row.getDateAuthorised())).append(',')
                    .append(escape(row.getReviewDate())).append(',')
                    .append(escape(row.getPractitionerName())).append(',')
                    .append(row.isReductionStrategyInPlace() ? "Yes" : "No").append(',')
                    .append(escape(joinIds(row.getIncidentIds()))).append(',')
                    .append(row.isStaffTrained() ? "Yes" : "No").append(',')
                    .append(escape(row.getComments())).append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportBehaviourDataCsv(
            String requesterRole, String requesterEmployeeId,
            String clientId, String from, String to, String intensity, Boolean followUpRequired, String q
    ) {
        List<BehaviourDataRecordDTO> rows = behaviourDataService.list(
                requesterRole, requesterEmployeeId, clientId, from, to, intensity, followUpRequired, q
        );
        StringBuilder csv = new StringBuilder(
                "Date,Time,Participant,Behaviour Observed,Antecedent,Staff Response,Restrictive Practice Used,Outcome,Duration,Intensity,Follow-Up Required,Staff Initials\n"
        );
        for (BehaviourDataRecordDTO row : rows) {
            csv.append(escape(row.getRecordDate())).append(',')
                    .append(escape(row.getRecordTime())).append(',')
                    .append(escape(row.getParticipantName())).append(',')
                    .append(escape(row.getBehaviourObserved())).append(',')
                    .append(escape(row.getAntecedent())).append(',')
                    .append(escape(row.getStaffResponse())).append(',')
                    .append(escape(row.getRestrictivePracticeUsed())).append(',')
                    .append(escape(row.getOutcome())).append(',')
                    .append(escape(row.getDuration())).append(',')
                    .append(escape(row.getIntensity())).append(',')
                    .append(row.isFollowUpRequired() ? "Yes" : "No").append(',')
                    .append(escape(row.getStaffInitials())).append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportEvidenceCsv(
            String requesterRole, String requesterEmployeeId,
            String clientId, String rpType, String from, String to, String q
    ) {
        List<RestrictivePracticeEvidenceDTO> rows = evidenceService.list(
                requesterRole, requesterEmployeeId, clientId, rpType, from, to, q
        );
        StringBuilder csv = new StringBuilder(
                "Participant,Restrictive Practice Type,Baseline Frequency,Current Frequency,Reduction Goal,Strategies,Alternative Supports,Staff Training Completed,Behaviour Data Reviewed,Review Date,Outcome/Progress,Next Actions,Responsible Person\n"
        );
        for (RestrictivePracticeEvidenceDTO row : rows) {
            csv.append(escape(row.getParticipantName())).append(',')
                    .append(escape(row.getRestrictivePracticeType())).append(',')
                    .append(escape(row.getBaselineFrequency())).append(',')
                    .append(escape(row.getCurrentFrequency())).append(',')
                    .append(escape(row.getReductionGoal())).append(',')
                    .append(escape(row.getStrategiesImplemented())).append(',')
                    .append(escape(row.getAlternativeSupportsUsed())).append(',')
                    .append(row.isStaffTrainingCompleted() ? "Yes" : "No").append(',')
                    .append(row.isBehaviourDataReviewed() ? "Yes" : "No").append(',')
                    .append(escape(row.getReviewDate())).append(',')
                    .append(escape(row.getOutcomeProgress())).append(',')
                    .append(escape(row.getNextActions())).append(',')
                    .append(escape(row.getResponsiblePersonName())).append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String joinIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return "";
        return ids.stream().collect(Collectors.joining("; "));
    }

    private String escape(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
