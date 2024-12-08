package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Appointment;
import com.skycareSolutions.skyCarePortal.model.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

public interface AppointmentRepository extends CrudRepository<Appointment, String> {

    // Get the last appointment id
    @Query(value = "SELECT ap.appointmentid FROM appointment ap ORDER BY CAST(SUBSTRING(ap.appointmentid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfAppointments();

    // Fetch all appointments
    @Query(value = "SELECT AP FROM Appointment AP")
    List<Appointment> findAllAppointments();

    // Fetch appointments for a specific client
    @Query("SELECT ap FROM Appointment ap WHERE ap.client = :client")
    List<Appointment> findAppointmentsByClient(Client client);

    // Fetch appointments within a date range (including appointments that overlap with the date range)
    @Query("SELECT ap FROM Appointment ap WHERE (ap.startDate BETWEEN :startDate AND :endDate) " +
            "OR (ap.endDate BETWEEN :startDate AND :endDate) " +
            "OR (ap.startDate <= :startDate AND ap.endDate >= :endDate)")
    List<Appointment> findAppointmentsWithinDateRange(Date startDate, Date endDate);

}
