package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.CareGiver;
import com.skycareSolutions.skyCarePortal.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CareGiverRepository extends JpaRepository<CareGiver, String> {

    @Query(value = "SELECT cg.care_giverid FROM caregiver cg ORDER BY CAST(SUBSTRING(cg.care_giverid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfcareGiver();

    @Query("SELECT cg from CareGiver cg where cg.employee=:emp")
    CareGiver getCareGiverByEmployee(Employee emp);
}
