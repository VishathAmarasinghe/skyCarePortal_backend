package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.RestrictivePracticeRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RestrictivePracticeRegisterRepository extends JpaRepository<RestrictivePracticeRegister, String> {
    @Query(value = "SELECT r.registerid FROM restrictive_practice_register r ORDER BY CAST(SUBSTRING(r.registerid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastRegisterId();
}
