package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.StaffTrainingRecordDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class StaffTrainingExportService {

    @Autowired
    private StaffTrainingRecordService staffTrainingRecordService;

    public byte[] exportCsv(
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
        List<StaffTrainingRecordDTO> rows = staffTrainingRecordService.list(
                requesterRole, requesterEmployeeId, employeeId, course, provider, expiryStatus,
                from, to, competencyAssessed, certificateFiled, q
        );
        StringBuilder csv = new StringBuilder(
                "Staff Name,Position,Training Course,Provider,Date Completed,Expiry Date,Certificate Filed,Competency Assessed,Assessor,Comments,Expiry Status\n"
        );
        for (StaffTrainingRecordDTO row : rows) {
            csv.append(escape(row.getEmployeeName())).append(',')
                    .append(escape(row.getPosition())).append(',')
                    .append(escape(row.getTrainingCourse())).append(',')
                    .append(escape(row.getProvider())).append(',')
                    .append(escape(row.getDateCompleted())).append(',')
                    .append(escape(row.getExpiryDate())).append(',')
                    .append(row.isCertificateFiled() ? "Yes" : "No").append(',')
                    .append(row.isCompetencyAssessed() ? "Yes" : "No").append(',')
                    .append(escape(row.getAssessorName())).append(',')
                    .append(escape(row.getComments())).append(',')
                    .append(escape(row.getExpiryStatus())).append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
