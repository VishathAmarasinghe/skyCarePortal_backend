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
@Table(name = "taskImportance")
public class TaskImportance {

    @Id
    @Column(name = "taskImportanceID", nullable = false, unique = true)
    private String taskImportanceID;

    @Column(name = "name", nullable = false)
    private String name;  // (e.g., "High", "Medium", "Low")
}
