package com.skycareSolutions.skyCarePortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationSettingDTO {
    private String id;
    private String name;
    private String abn;
    private String address;
    private String phone;
    private String email;
    private String directorName;
    private String logoPath;
    private String logoUrl;
    private String updatedAt;
}
