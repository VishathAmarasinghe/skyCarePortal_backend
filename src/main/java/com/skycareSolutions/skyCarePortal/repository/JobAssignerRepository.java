package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Appointment;
import com.skycareSolutions.skyCarePortal.model.CareGiver;
import com.skycareSolutions.skyCarePortal.model.JobAssigner;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobAssignerRepository extends CrudRepository<JobAssigner,String> {

    @Query(value = "SELECT ja.assignid FROM job_assigner ja ORDER BY CAST(SUBSTRING(ja.assignid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfJobAssigner();

    @Query("SELECT js FROM JobAssigner js WHERE appointment=:appointment")
    List<JobAssigner> findJobAssignersByAppointment(Appointment appointment);

    @Query("SELECT js from JobAssigner js WHERE careGiver=:careGiver AND acceptanceType=:status")
    List<JobAssigner> findJobsAssignedToCareGiverBasedOnStatus(CareGiver careGiver,String status);

    @Query("SELECT COUNT(js.careGiver) AS seatCount from JobAssigner js WHERE appointment=:appointment AND acceptanceType=:status")
    int findNumberOfJobAssignersBasedOnAcceptanceType(Appointment appointment,String status);

    @Modifying
    @Transactional
    @Query("UPDATE JobAssigner j SET j.acceptanceType = :acceptanceType WHERE j.assignID = :assignID")
    int updateAcceptanceType(@Param("assignID") String assignID, @Param("acceptanceType") String acceptanceType);


}
