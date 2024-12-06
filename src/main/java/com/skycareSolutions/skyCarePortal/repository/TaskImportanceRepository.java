package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.TaskImportance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskImportanceRepository extends JpaRepository<TaskImportance,String> {

    @Query(value = "SELECT ti.task_importanceid FROM task_importance ti ORDER BY CAST(SUBSTRING(ti.task_importanceid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfTaskImportance();
}
