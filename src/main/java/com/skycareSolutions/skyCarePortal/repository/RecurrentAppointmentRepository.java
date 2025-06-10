package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Appointment;
import com.skycareSolutions.skyCarePortal.model.RecurrentAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface RecurrentAppointmentRepository extends JpaRepository<RecurrentAppointment, String> {

    @Query(value = "SELECT e.recurrent_appointmentid FROM recurrent_appointment e ORDER BY CAST(SUBSTRING(e.recurrent_appointmentid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfRecurrentAppointment();

    // Get all recurrent appointments for a given appointment ID
    @Query("SELECT r FROM RecurrentAppointment r WHERE r.appointment.appointmentID = :appointmentID")
    List<RecurrentAppointment> findAllByAppointmentID(String appointmentID);

    // Delete all recurrent appointments for a given appointment ID
    @Modifying
    @Query("DELETE FROM RecurrentAppointment r WHERE r.appointment.appointmentID = :appointmentID")
    void deleteByAppointmentID(String appointmentID);

    // Fetch appointments within a date range (including appointments that overlap with the date range)
    @Query("SELECT DISTINCT r FROM RecurrentAppointment r WHERE r.appointment.appointmentID = :appointmentID AND " +
            "((r.startDate BETWEEN :startDate AND :endDate) " +
            "OR (r.endDate BETWEEN :startDate AND :endDate) " +
            "OR (r.startDate <= :startDate AND r.endDate >= :endDate))")
    List<RecurrentAppointment> findAppointmentsWithinDateRangeAndAppointment(
            LocalDate startDate,
            LocalDate endDate,
            String appointmentID
    );

    // Fetch appointments within a date range (including appointments that overlap with the date range)
    @Query("SELECT r FROM RecurrentAppointment r WHERE (r.startDate BETWEEN :startDate AND :endDate) " +
            "OR (r.endDate BETWEEN :startDate AND :endDate) " +
            "OR (r.startDate <= :startDate AND r.endDate >= :endDate)")
    List<RecurrentAppointment> findAppointmentsWithinDateRange(LocalDate startDate, LocalDate endDate);

    @Modifying
    @Query("UPDATE RecurrentAppointment r SET r.status = 'cancelled', r.isCancelled = true WHERE r = :recurrentAppointment")
    void cancelRecurrentAppointmentById(RecurrentAppointment recurrentAppointment);

    @Modifying
    @Query("UPDATE RecurrentAppointment r SET r.status =:state WHERE r = :recurrentAppointment")
    void updateStateOfRecurrentAppointmentById(RecurrentAppointment recurrentAppointment,String state);

    @Modifying
    @Query("UPDATE RecurrentAppointment r SET r.startDate =:startDate, r.startTime=:startTime,r.endDate=:endDate,r.endTime=:endTime, r.comment=:comment WHERE r = :recurrentAppointment")
    void updateRecurrentAppointment(LocalDate startDate,LocalDate endDate,String startTime,String endTime,String comment,RecurrentAppointment recurrentAppointment);

}
