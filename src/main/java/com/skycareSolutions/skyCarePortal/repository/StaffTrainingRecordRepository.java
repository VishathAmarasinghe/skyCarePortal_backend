package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.StaffTrainingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffTrainingRecordRepository extends JpaRepository<StaffTrainingRecord, String> {

    @Query(value = "SELECT r.recordid FROM staff_training_record r ORDER BY CAST(SUBSTRING(r.recordid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastRecordId();
}
