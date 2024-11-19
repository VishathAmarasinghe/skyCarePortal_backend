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
@Table(name = "noteDocuments")
public class NoteDocuments {

    @EmbeddedId
    private NoteDocumentsKey id;

    @Column(name = "document", nullable = false, columnDefinition = "TEXT")
    private String document;

    @ManyToOne
    @MapsId("noteID") // Maps the noteID part of the composite key
    @JoinColumn(name = "noteID", nullable = false)
    private Notes note;
}
