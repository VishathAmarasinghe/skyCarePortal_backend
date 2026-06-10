package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.StaffTrainingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffTrainingRecordRepository extends JpaRepository<StaffTrainingRecord, String> {

    @Query(value = "SELECT r.recordid FROM staff_training_record r ORDER BY CAST(SUBSTRING(r.recordid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastRecordId();

    @Query("SELECT r FROM StaffTrainingRecord r WHERE r.employee.employeeID = :employeeId")
    List<StaffTrainingRecord> findByEmployeeId(@Param("employeeId") String employeeId);
}
