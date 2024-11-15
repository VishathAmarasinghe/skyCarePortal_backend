package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ClientClassificationID implements Serializable {

    @Column(name = "clientID")
    private String clientID;

    @Column(name = "classificationID")
    private String classificationID;

    // Default constructor
    public ClientClassificationID() {}

    // Constructor with fields
    public ClientClassificationID(String clientID, String classificationID) {
        this.clientID = clientID;
        this.classificationID = classificationID;
    }

    // Getters and Setters
    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getClassificationID() {
        return classificationID;
    }

    public void setClassificationID(String classificationID) {
        this.classificationID = classificationID;
    }

    // Override equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientClassificationID that = (ClientClassificationID) o;
        return Objects.equals(clientID, that.clientID) && Objects.equals(classificationID, that.classificationID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientID, classificationID);
    }
}
