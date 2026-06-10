package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.OrganizationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationSettingRepository extends JpaRepository<OrganizationSetting, String> {
}
