package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.AppointmentAddress;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AppointmentAddressRepository extends CrudRepository<AppointmentAddress,String> {
    // Get the last appointmentAddress id
    @Query(value = "SELECT ap.appointment_addressid FROM appointment_address ap ORDER BY CAST(SUBSTRING(ap.appointment_addressid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfAppointmentAddress();
}
