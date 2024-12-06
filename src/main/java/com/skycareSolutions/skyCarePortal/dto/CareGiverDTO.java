package com.skycareSolutions.skyCarePortal.dto;

import com.skycareSolutions.skyCarePortal.model.CareGiverPayments;
import lombok.Data;

import java.util.List;

@Data
public class CareGiverDTO {
    private String careGiverID;
    private EmployeeDTO employee;
    private String status;
    private List<CareGiverPaymentsDTO> careGiverPayments;
    private List<CareGiverDocumentsDTO> careGiverDocuments;
}
