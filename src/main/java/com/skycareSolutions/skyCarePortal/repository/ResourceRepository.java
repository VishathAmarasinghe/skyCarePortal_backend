package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResourceRepository extends JpaRepository<Resource, String> {

    @Query(value = "SELECT r.resource_id FROM resource r ORDER BY CAST(SUBSTRING(r.resource_id, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfResource();

}