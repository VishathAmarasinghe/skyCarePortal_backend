package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ClientPhoneNoId implements Serializable {

    @Column(name = "clientID")
    private String clientID;

    @Column(name = "phoneNo")
    private String phoneNo;

    // Default constructor
    public ClientPhoneNoId() {}

    // Constructor with fields
    public ClientPhoneNoId(String clientID, String phoneNo) {
        this.clientID = clientID;
        this.phoneNo = phoneNo;
    }

    // Getters and Setters
    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    // Override equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientPhoneNoId that = (ClientPhoneNoId) o;
        return Objects.equals(clientID, that.clientID) && Objects.equals(phoneNo, that.phoneNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientID, phoneNo);
    }
}
