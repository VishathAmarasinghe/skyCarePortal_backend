package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.CarePlan;
import com.skycareSolutions.skyCarePortal.model.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CarePlanRepository extends CrudRepository<CarePlan,String> {
    // Find carePlan by client entity (not just clientID)
    @Query("SELECT cp FROM CarePlan cp WHERE cp.clientID = :client")
    List<CarePlan> findByClient(Client client);

    // Get all careplans
    @Query("SELECT cp FROM CarePlan cp")
    List<CarePlan> findAll();

    // Get the last careplan id
    @Query(value = "SELECT cp.careplanid FROM careplan cp ORDER BY CAST(SUBSTRING(cp.careplanid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfCarePlan();
}
