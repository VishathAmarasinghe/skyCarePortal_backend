package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "email_templates")
public class EmailTemplates {

    @Id
    @Column(name = "templateID")  // Added column annotation
    private String templateID;

    @Column(name = "title")  // Column for the title of the template
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")  // Store HTML content as TEXT
    private String content;
}
