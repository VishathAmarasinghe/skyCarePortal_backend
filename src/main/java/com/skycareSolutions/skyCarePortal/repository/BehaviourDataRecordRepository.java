package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.BehaviourDataRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BehaviourDataRecordRepository extends JpaRepository<BehaviourDataRecord, String> {
    @Query(value = "SELECT r.recordid FROM behaviour_data_record r ORDER BY CAST(SUBSTRING(r.recordid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastRecordId();
}
