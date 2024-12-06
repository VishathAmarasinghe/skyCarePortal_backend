package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Appointment;
import com.skycareSolutions.skyCarePortal.model.AppointmentAttachments;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AppointmentAttachmentsRepository extends CrudRepository<AppointmentAttachments,String> {

    @Query("SELECT ap FROM AppointmentAttachments ap WHERE ap.appointment=:appointment")
    List<AppointmentAttachments> getAppointmentAttachmentByAppointmentID(Appointment appointment);
}
