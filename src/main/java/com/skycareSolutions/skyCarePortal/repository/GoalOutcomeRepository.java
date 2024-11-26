package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.CarePlan;
import com.skycareSolutions.skyCarePortal.model.GoalOutcome;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GoalOutcomeRepository extends CrudRepository<GoalOutcome,String> {


    // Get all goal outcomes
    @Query("SELECT oc FROM GoalOutcome oc")
    List<GoalOutcome> findAll();

    // Get the last goaloutcome id
    @Query(value = "SELECT oc.goal_outcomeid FROM goal_outcome oc ORDER BY CAST(SUBSTRING(oc.goal_outcomeid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfGoalOutcome();
}
