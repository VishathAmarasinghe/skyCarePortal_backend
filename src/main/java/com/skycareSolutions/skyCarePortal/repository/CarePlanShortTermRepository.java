package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.CarePlan;
import com.skycareSolutions.skyCarePortal.model.CarePlanLongTermGoal;
import com.skycareSolutions.skyCarePortal.model.CarePlanShortTermGoal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CarePlanShortTermRepository extends CrudRepository<CarePlanShortTermGoal,String> {
    // Find shortTermgoal by careplan
    @Query("SELECT cps FROM CarePlanShortTermGoal  cps WHERE cps.carePlanID = :carePlan")
    List<CarePlanShortTermGoal> findbyCarePlanID(CarePlan carePlan);

    // Find short term goal by longtermID
    @Query("SELECT cps FROM CarePlanShortTermGoal  cps WHERE cps.longTermGoalID = :longTermGoal")
    List<CarePlanShortTermGoal> findbyLongTermGoalID(CarePlanLongTermGoal longTermGoal);

    // Get all long term careplans
    @Query("SELECT cps FROM CarePlanShortTermGoal cps")
    List<CarePlanShortTermGoal> findAllCarePlans();

    // Get the last careplan id
    @Query(value = "SELECT cps.short_term_goalid FROM care_plan_short_term_goal cps ORDER BY CAST(SUBSTRING(cps.short_term_goalid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfShortTermCarePlan();
}
