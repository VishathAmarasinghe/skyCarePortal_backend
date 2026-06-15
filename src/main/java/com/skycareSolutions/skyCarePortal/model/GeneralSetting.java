package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "general_setting")
public class GeneralSetting {

    public static final String SINGLETON_ID = "GENERAL";

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "block_timesheet_on_document_non_compliance")
    private boolean blockTimesheetOnDocumentNonCompliance;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
}
