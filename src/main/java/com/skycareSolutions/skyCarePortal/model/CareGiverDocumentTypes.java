package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "caregiverDocumentTypes")
public class CareGiverDocumentTypes {

    @Id
    @Column(name = "documentTypeID", nullable = false, unique = true)
    private String documentTypeID;  // Primary Key (not auto-generated)

    @Column(name = "documentName", nullable = false)
    private String documentName;  // Name of the document type (e.g., ID card, License)

    @Column(name = "expDateNeeded")
    private boolean expDateNeeded;

    @Column(name = "required", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean required;

    @Column(name = "status")
    private String status;

}
