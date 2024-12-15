package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.IncidentActionTypeAllAnswers;
import com.skycareSolutions.skyCarePortal.model.Incidents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentActionTypeAllAnswersRepository extends JpaRepository<IncidentActionTypeAllAnswers, Long> {

    // Find answers by IncidentActionTypes
    List<IncidentActionTypeAllAnswers> findByIncidentActionTypes_IncidentActionID(String incidentActionID);

    // Find answers by IncidentSubActionTypes
    List<IncidentActionTypeAllAnswers> findByIncidentSubActionTypes_Id(Long incidentSubActionID);

    // Find answers by Incidents
    @Query("SELECT ans FROM IncidentActionTypeAllAnswers ans WHERE ans.incidents =: incidents")
    List<IncidentActionTypeAllAnswers> findByIncidents_Id(Incidents incidents);
}
