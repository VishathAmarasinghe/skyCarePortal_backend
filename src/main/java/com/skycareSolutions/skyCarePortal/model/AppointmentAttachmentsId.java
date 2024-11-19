package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class AppointmentAttachmentsId implements Serializable {

    @Column(name = "appointmentID")
    private String appointmentID;

    @Column(name = "documentID")
    private String documentID;

    // Default constructor
    public AppointmentAttachmentsId() {}

    // Constructor with fields
    public AppointmentAttachmentsId(String appointmentID, String documentID) {
        this.appointmentID = appointmentID;
        this.documentID = documentID;
    }

    // hashCode and equals methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentAttachmentsId that = (AppointmentAttachmentsId) o;
        return Objects.equals(appointmentID, that.appointmentID) &&
                Objects.equals(documentID, that.documentID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointmentID, documentID);
    }
}
