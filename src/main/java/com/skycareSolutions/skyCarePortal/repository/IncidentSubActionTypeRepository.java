package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.IncidentSubActionTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IncidentSubActionTypeRepository extends JpaRepository<IncidentSubActionTypes, Long> {

    // Retrieve all subactions by incidentActionID
    @Query("SELECT sub FROM IncidentSubActionTypes sub WHERE sub.incidentActionTypes.incidentActionID = :incidentActionID")
    List<IncidentSubActionTypes> findAllByIncidentActionID(@Param("incidentActionID") String incidentActionID);

}
