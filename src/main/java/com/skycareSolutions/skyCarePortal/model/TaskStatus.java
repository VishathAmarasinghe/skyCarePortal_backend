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
@Table(name = "taskStatus")
public class TaskStatus {

    @Id
    @Column(name = "taskStatusID", nullable = false, unique = true)
    private String taskStatusID;

    @Column(name = "status", nullable = false)
    private String status;  // (e.g., "Pending", "Completed")
}
