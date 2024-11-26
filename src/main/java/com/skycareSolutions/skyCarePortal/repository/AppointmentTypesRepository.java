package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Appointment;
import com.skycareSolutions.skyCarePortal.model.AppointmentType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AppointmentTypesRepository extends CrudRepository<AppointmentType,String> {
    // Get the last appointment type id
    @Query(value = "SELECT ap.appointmentid FROM appointment ap ORDER BY CAST(SUBSTRING(ap.appointmentid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfAppointmentType();

    @Query(value = "SELECT AP FROM AppointmentType")
    List<AppointmentType> findAllAppointmentTypes();


}
