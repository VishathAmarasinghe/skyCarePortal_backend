package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Client;
import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.model.Incidents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incidents,String> {

    @Query(value = "SELECT at.incidentid FROM incidents at ORDER BY CAST(SUBSTRING(at.incidentid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfIncident();

    @Query("SELECT ats FROM Incidents ats WHERE ats.employee=:employee")
    List<Incidents> getIncidentsRelatedToEmployee(Employee employee);

    @Query("SELECT i FROM Incidents i WHERE i.incidentDate = :incidentDate " +
           "AND i.incidentTime = :incidentTime " +
           "AND (i.client = :client OR (i.client IS NULL AND :client IS NULL)) " +
           "AND i.employee = :employee")
    Optional<Incidents> findDuplicateIncident(
            @Param("incidentDate") LocalDate incidentDate,
            @Param("incidentTime") String incidentTime,
            @Param("client") Client client,
            @Param("employee") Employee employee
    );
}
