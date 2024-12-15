package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.IncidentInvolvedParties;
import com.skycareSolutions.skyCarePortal.model.Incidents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IncidentInvolvedPartiesRepository extends JpaRepository<IncidentInvolvedParties,String> {

    @Query(value = "SELECT e.partyid FROM incident_involved_parties e ORDER BY CAST(SUBSTRING(e.partyid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfIncidentInvolvedParties();

    @Query("SELECT e from IncidentInvolvedParties e where incidents=:incidents")
    List<IncidentInvolvedParties> getIncidentInvolvedPartiesByIncidentID(Incidents incidents);
}
