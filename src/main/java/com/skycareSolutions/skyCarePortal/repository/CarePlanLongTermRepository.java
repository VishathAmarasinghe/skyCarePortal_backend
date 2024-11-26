package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.CarePlan;
import com.skycareSolutions.skyCarePortal.model.CarePlanLongTermGoal;
import com.skycareSolutions.skyCarePortal.model.ClientType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CarePlanLongTermRepository extends CrudRepository<CarePlanLongTermGoal,String> {
    // Find carePlan by client id
    @Query("SELECT cpl FROM CarePlanLongTermGoal  cpl WHERE cpl.carePlanID = :carePlan")
    List<CarePlanLongTermGoal> findLongTermCarePlansByCarePlanID(CarePlan carePlan);

    // Get all long term careplans
    @Query("SELECT cpl FROM CarePlanLongTermGoal  cpl")
    List<CarePlanLongTermGoal> findAllCarePlans();

    // Get the last careplan id
    @Query(value = "SELECT cpl.long_goalid FROM care_plan_long_term_goal cpl ORDER BY CAST(SUBSTRING(cpl.long_goalid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfLongTermCarePlan();
}
