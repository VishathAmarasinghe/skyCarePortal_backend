package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

@Data
public class CareGiverPaymentsDTO {

    private String careGiverID;
    private String paymentTypeID;
    private double amount;
}
