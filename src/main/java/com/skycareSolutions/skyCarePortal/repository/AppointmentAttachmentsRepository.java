package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.AppointmentAttachments;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AppointmentAttachmentsRepository extends CrudRepository<AppointmentAttachments,String> {

}
