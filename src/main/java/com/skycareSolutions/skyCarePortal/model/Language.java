package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "language")
public class Language {

    @Id
    @Column(name = "languageID")
    private String languageID;
    @Column(name = "language")
    private String language;
    @Column(name = "languageNotes")
    private String languageNotes;

    @ManyToMany(mappedBy = "language")
    private List<ClientLanguage> clientLanguages;

    // Getters and Setters
}
