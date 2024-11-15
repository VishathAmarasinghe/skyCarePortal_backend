package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ClientLanguageId implements Serializable {

    @Column(name = "clientID")
    private String clientID;

    @Column(name = "languageID")
    private String languageID;

    // Default constructor
    public ClientLanguageId() {}

    // Constructor with fields
    public ClientLanguageId(String clientID, String languageID) {
        this.clientID = clientID;
        this.languageID = languageID;
    }

    // Getters and Setters
    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getLanguageID() {
        return languageID;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }

    // Override equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientLanguageId that = (ClientLanguageId) o;
        return Objects.equals(clientID, that.clientID) && Objects.equals(languageID, that.languageID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientID, languageID);
    }

    @PrePersist
    public void validateClientLanguageId() {
        if (clientID == null || languageID == null) {
            throw new IllegalArgumentException("clientID and languageID cannot be null");
        }
    }
}
