package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Appointment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AppointmentRepository extends CrudRepository<Appointment,String> {
    // Get the last appointment id
    @Query(value = "SELECT ap.appointmentid FROM appointment ap ORDER BY CAST(SUBSTRING(ap.appointmentid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfAppointments();

    @Query(value = "SELECT AP FROM Appointment AP")
    List<Appointment> findAllAppointments();}
