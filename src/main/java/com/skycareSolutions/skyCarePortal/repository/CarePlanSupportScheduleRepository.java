package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.CarePlan;
import com.skycareSolutions.skyCarePortal.model.CarePlanSupportSchedule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CarePlanSupportScheduleRepository extends CrudRepository<CarePlanSupportSchedule, String> {

    @Query("SELECT cps FROM CarePlanSupportSchedule cps WHERE cps.careplanID = :carePlan")
    List<CarePlanSupportSchedule> findAllByCarePlan(CarePlan carePlan);

    @Query(value = "SELECT cps.scheduleid FROM careplan_support_schedule cps ORDER BY CAST(SUBSTRING(cps.scheduleid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfCarePlanSupportSchedule();
}

