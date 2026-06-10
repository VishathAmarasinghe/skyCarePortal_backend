package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.AgreementTemplateAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AgreementTemplateAssignmentRepository extends JpaRepository<AgreementTemplateAssignment, String> {

    List<AgreementTemplateAssignment> findAllByOrderByPriorityAsc();

    void deleteByTemplate_Id(String templateId);

    @Query(value = "SELECT id FROM agreement_template_assignment ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String getLastId();
}
