package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.TaskAttachements;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskAttachmentRepository extends JpaRepository<TaskAttachements,String> {
    
}
