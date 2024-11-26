package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.RecurrentWork;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RecurrentWorkRepository extends CrudRepository<RecurrentWork,String> {
    // Get the last recurrentProcess id
    @Query(value = "SELECT rp.recurrent_workid FROM recurrent_work ap ORDER BY CAST(SUBSTRING(ap.recurrent_workid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfRecurrentTask();
}
