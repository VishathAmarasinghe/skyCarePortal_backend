package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "appointmentAttachments")
public class AppointmentAttachments {

    @EmbeddedId
    private AppointmentAttachmentsId id;

    @ManyToOne
    @JoinColumn(name = "appointmentID", insertable = false, updatable = false)
    private Appointment appointment;


    @Column(name = "document")
    private String document;
}
