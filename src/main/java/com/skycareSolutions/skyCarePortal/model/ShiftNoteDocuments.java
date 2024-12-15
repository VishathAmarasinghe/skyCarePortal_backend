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
@Table(name = "shiftNotesDocuments")
public class ShiftNoteDocuments {
    @Id
    private String documentId;

    private String documentName;

    private String documentLocation;

    @ManyToOne
    @JoinColumn(name = "shiftNoteID", nullable = false)
    private ShiftNotes shiftNotes;
}
