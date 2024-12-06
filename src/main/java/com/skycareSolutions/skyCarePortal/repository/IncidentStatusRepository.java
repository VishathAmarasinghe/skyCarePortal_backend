package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentStatusRepository extends JpaRepository<IncidentStatus, String> {

    @Query(value = "SELECT at.incident_statusid FROM incident_status at ORDER BY CAST(SUBSTRING(at.incident_statusid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfIncidentStatus();
}
