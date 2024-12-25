package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.model.Incidents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incidents,String> {

    @Query(value = "SELECT at.incidentid FROM incidents at ORDER BY CAST(SUBSTRING(at.incidentid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfIncident();

    @Query("SELECT ats FROM Incidents ats WHERE ats.employee=:employee")
    List<Incidents> getIncidentsRelatedToEmployee(Employee employee);
}
