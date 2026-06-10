package com.skycareSolutions.skyCarePortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "organization_setting")
public class OrganizationSetting {

    public static final String SINGLETON_ID = "ORG_SETTINGS";

    @Id
    @Column(name = "id", length = 30)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "abn")
    private String abn;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "director_name")
    private String directorName;

    @Column(name = "logo_path")
    private String logoPath;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
