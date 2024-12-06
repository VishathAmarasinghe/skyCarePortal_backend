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
@Table(name = "taskItems")
public class TaskItems {

    @Id
    @Column(name = "itemID")
    private String itemID;

    @ManyToOne
    @JoinColumn(name = "taskID",nullable = false)
    private Task taskID;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;
}
