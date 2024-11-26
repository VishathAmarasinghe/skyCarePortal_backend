package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.CarePlan;
import com.skycareSolutions.skyCarePortal.model.CarePlanBilables;
import com.skycareSolutions.skyCarePortal.model.CarePlanLongTermGoal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CarePlanBillableRepository extends CrudRepository<CarePlanBilables,String> {

    @Query("SELECT cpb FROM CarePlanBilables cpb WHERE cpb.careplanID = :carePlan")
    List<CarePlanBilables> findAllBillablesByCarePlanID(CarePlan carePlan);

    // Get all care plan billable
    @Query("SELECT cpb FROM CarePlanBilables cpb")
    List<CarePlanBilables> findAllCarePlanBillables();

    // Get the last careplanbillable id
    @Query(value = "SELECT cpb.billid FROM careplan_bilables cpb ORDER BY CAST(SUBSTRING(cpb.billid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfCarePlanBillables();
}
