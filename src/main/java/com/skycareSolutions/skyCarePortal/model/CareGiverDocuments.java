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
@Table(name = "caregiverDocuments")
public class CareGiverDocuments {

    @EmbeddedId
    private CareGiverDocumentsId id;  // Composite primary key (careGiverID + documentTypeID)

    @ManyToOne
    @JoinColumn(name = "careGiverID", insertable = false, updatable = false)
    private CareGiver careGiver;  // Foreign key to CareGiver

    @ManyToOne
    @JoinColumn(name = "documentTypeID", insertable = false, updatable = false)
    private CareGiverDocumentTypes careGiverDocumentType;  // Foreign key to CareGiverDocumentTypes

    @Column(name = "expDate")
    private Date expDate;  // Expiration date of the document

    @Column(name = "document")
    private String document;

    @Column(name = "status")
    private String status;  // Status of the document (e.g., Active, Expired)
}
