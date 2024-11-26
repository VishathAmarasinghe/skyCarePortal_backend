package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.CarePlan;
import com.skycareSolutions.skyCarePortal.model.CarePlanStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CarePlanStatusRepository extends CrudRepository<CarePlanStatus,String> {
    // Get all careplansStatus
    @Query("SELECT cps FROM CarePlanStatus cps")
    List<CarePlanStatus> findAll();

    // Get the last careplan status id
    @Query(value = "SELECT cps.careplan_statusid FROM careplan_status cps ORDER BY CAST(SUBSTRING(cps.careplan_statusid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfCarePlanStatus();
}
