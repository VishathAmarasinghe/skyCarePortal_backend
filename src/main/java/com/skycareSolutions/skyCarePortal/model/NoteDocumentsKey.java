package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class NoteDocumentsKey implements Serializable {

    @Column(name = "noteID")
    private String noteID;

    @Column(name = "docID")
    private String docID;
}
