package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.TaskItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskItemRepository extends JpaRepository<TaskItems,String> {
    @Query(value = "SELECT ti.itemid FROM task_items ti ORDER BY CAST(SUBSTRING(ti.itemid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfTaskItems();
}
