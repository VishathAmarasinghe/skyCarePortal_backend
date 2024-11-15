package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client_language")
public class ClientLanguage implements Serializable {

    @EmbeddedId
    private ClientLanguageId id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @MapsId("clientID")
    @JoinColumn(name = "clientID")
    private Client client;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @MapsId("languageID")
    @JoinColumn(name = "languageID")
    private Language language;

    public ClientLanguage(ClientLanguageId id,Client client,Language language){
        this.client=client;
        this.language=language;
        this.id=id;
    }

    @PrePersist
    public void validateClientLanguageAssociations() {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }
    }


    // Getters and Setters
}
