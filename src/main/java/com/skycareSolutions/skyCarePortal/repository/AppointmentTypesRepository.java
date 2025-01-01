package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Appointment;
import com.skycareSolutions.skyCarePortal.model.AppointmentType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AppointmentTypesRepository extends CrudRepository<AppointmentType,String> {
    // Get the last appointment type id
    @Query(value = "SELECT ap.appointment_typeid FROM appointment_type ap ORDER BY CAST(SUBSTRING(ap.appointment_typeid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfAppointmentType();

    @Query(value = "SELECT AP FROM AppointmentType AP")
    List<AppointmentType> findAllAppointmentTypes();


}
