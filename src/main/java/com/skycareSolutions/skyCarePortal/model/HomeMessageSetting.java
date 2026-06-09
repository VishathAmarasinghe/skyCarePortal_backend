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
@Table(name = "home_message_setting")
public class HomeMessageSetting {

    @Id
    @Column(name = "id")
    private String id; // always "HOME_MESSAGE"

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "type")
    private String type; // WARNING | IMPORTANT | GENERAL

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
}

