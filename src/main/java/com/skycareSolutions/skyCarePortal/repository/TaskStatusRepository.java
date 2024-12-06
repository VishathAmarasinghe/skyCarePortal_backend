package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskStatusRepository extends JpaRepository<TaskStatus,String> {

    @Query(value = "SELECT ts.task_statusid FROM task_status ts ORDER BY CAST(SUBSTRING(ts.task_statusid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfTaskStatus();
}
