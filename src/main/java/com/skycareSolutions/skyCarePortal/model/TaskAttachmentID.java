package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
@Embeddable
public class TaskAttachmentID implements Serializable {

    private String taskID;
    private String documentID;

    // Default constructor
    public TaskAttachmentID() {}

    // Constructor with fields
    public TaskAttachmentID(String taskID, String documentID) {
        this.taskID = taskID;
        this.documentID = documentID;
    }

    // Getters and Setters
    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    // Equals and HashCode methods (important for composite keys)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskAttachmentID that = (TaskAttachmentID) o;

        if (!taskID.equals(that.taskID)) return false;
        return documentID.equals(that.documentID);
    }

    @Override
    public int hashCode() {
        int result = taskID.hashCode();
        result = 31 * result + documentID.hashCode();
        return result;
    }
}
