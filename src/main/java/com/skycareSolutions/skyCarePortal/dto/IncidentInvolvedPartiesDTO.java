package com.skycareSolutions.skyCarePortal.dto;
import lombok.Data;

@Data
public class IncidentInvolvedPartiesDTO {
    private String partyID;

    private String firstName;

    private String lastName;

    private String email;

    private String workPhoneNo;

    private String otherPhoneNo;

    private AppointmentAddressDTO appointmentAddress;

    private String incidentID;

    private String type;
}
