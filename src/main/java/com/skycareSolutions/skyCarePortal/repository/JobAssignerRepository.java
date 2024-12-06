package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Appointment;
import com.skycareSolutions.skyCarePortal.model.JobAssigner;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JobAssignerRepository extends CrudRepository<JobAssigner,String> {

    @Query(value = "SELECT ja.assignid FROM job_assigner ja ORDER BY CAST(SUBSTRING(ja.assignid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfJobAssigner();

    @Query("SELECT js FROM JobAssigner js WHERE appointment=:appointment")
    List<JobAssigner> findJobAssignersByAppointment(Appointment appointment);
}
