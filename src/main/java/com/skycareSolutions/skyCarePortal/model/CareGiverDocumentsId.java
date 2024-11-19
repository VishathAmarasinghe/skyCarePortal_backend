package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CareGiverDocumentsId implements Serializable {

    private String careGiverID;  // Foreign key from CareGiver
    private String documentTypeID;  // Foreign key from CareGiverDocumentTypes

    // Default constructor
    public CareGiverDocumentsId() {}

    // Constructor with fields
    public CareGiverDocumentsId(String careGiverID, String documentTypeID) {
        this.careGiverID = careGiverID;
        this.documentTypeID = documentTypeID;
    }

    // Getters and setters
    public String getCareGiverID() {
        return careGiverID;
    }

    public void setCareGiverID(String careGiverID) {
        this.careGiverID = careGiverID;
    }

    public String getDocumentTypeID() {
        return documentTypeID;
    }

    public void setDocumentTypeID(String documentTypeID) {
        this.documentTypeID = documentTypeID;
    }

    // Equals and HashCode methods (important for composite keys)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CareGiverDocumentsId that = (CareGiverDocumentsId) o;

        if (!careGiverID.equals(that.careGiverID)) return false;
        return documentTypeID.equals(that.documentTypeID);
    }

    @Override
    public int hashCode() {
        int result = careGiverID.hashCode();
        result = 31 * result + documentTypeID.hashCode();
        return result;
    }
}
