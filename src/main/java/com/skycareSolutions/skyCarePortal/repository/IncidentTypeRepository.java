package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.IncidentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentTypeRepository extends JpaRepository<IncidentType, String> {

    @Query(value = "SELECT e.incident_typeid FROM incident_type e ORDER BY CAST(SUBSTRING(e.incident_typeid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfIncidentType();
}
