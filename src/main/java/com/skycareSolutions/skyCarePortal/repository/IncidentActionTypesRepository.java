package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.IncidentActionTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentActionTypesRepository extends JpaRepository<IncidentActionTypes, String> {
    @Query(value = "SELECT at.incident_actionid FROM incident_action_types at ORDER BY CAST(SUBSTRING(at.incident_actionid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfIncidentActionTypes();
}
