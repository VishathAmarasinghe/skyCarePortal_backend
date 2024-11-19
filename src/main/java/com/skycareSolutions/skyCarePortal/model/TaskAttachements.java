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
@Table(name = "taskAttachments")
public class TaskAttachements {
    @EmbeddedId
    private TaskAttachmentID id;

    @ManyToOne
    @JoinColumn(name = "TaskID", insertable = false, updatable = false)
    private Task task;

    @Column(name = "document")
    private String document;
}
