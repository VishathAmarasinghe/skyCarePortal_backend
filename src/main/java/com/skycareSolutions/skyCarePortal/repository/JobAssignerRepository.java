package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Appointment;
import com.skycareSolutions.skyCarePortal.model.CareGiver;
import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.model.JobAssigner;
import com.skycareSolutions.skyCarePortal.model.RecurrentAppointment;
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

    @Query("SELECT js from JobAssigner js WHERE careGiver=:careGiver")
    List<JobAssigner> findJobsAssignedToCareGiver(CareGiver careGiver);

    @Query("SELECT js.careGiver.careGiverID FROM JobAssigner js WHERE js.appointment = :appointment")
    List<String> findCareGiverListAssociatedWithAppointment(@Param("appointment") Appointment appointment);

    @Query("SELECT COUNT(js.careGiver) AS seatCount from JobAssigner js WHERE appointment=:appointment AND recurrentAppointment=:recurrentAppointment AND acceptanceType=:status")
    int findNumberOfJobAssignersBasedOnAcceptanceType(Appointment appointment, String status, RecurrentAppointment recurrentAppointment);

    @Modifying
    @Transactional
    @Query("UPDATE JobAssigner j SET j.acceptanceType = :acceptanceType WHERE j.assignID = :assignID")
    int updateAcceptanceType(@Param("assignID") String assignID, @Param("acceptanceType") String acceptanceType);

    @Query("SELECT js FROM JobAssigner js WHERE recurrentAppointment=:recurrentAppointment")
    List<JobAssigner> findAllJobAssignersByRecurrentAppointmentID(RecurrentAppointment recurrentAppointment);

    @Query("SELECT js FROM JobAssigner js WHERE recurrentAppointment=:recurrentAppointment AND careGiver=:caregiver")
    JobAssigner findJobAssignWithRecurrentAppointmentAndCareGiver(RecurrentAppointment recurrentAppointment,CareGiver caregiver);

    @Query("SELECT js FROM JobAssigner js WHERE recurrentAppointment=:recurrentAppointment")
    List<JobAssigner> findJobAssignWithRecurrentAppointment(RecurrentAppointment recurrentAppointment);

    @Query("SELECT j FROM JobAssigner j WHERE j.employee = :employee")
    List<JobAssigner> findByAssignerEmployee(@Param("employee") Employee employee);

}
