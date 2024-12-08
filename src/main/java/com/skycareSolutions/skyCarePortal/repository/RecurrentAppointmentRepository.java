package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.RecurrentAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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
}
