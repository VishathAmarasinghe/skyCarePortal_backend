package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.AgreementInstanceReferenceMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AgreementInstanceReferenceMaterialRepository
        extends JpaRepository<AgreementInstanceReferenceMaterial, String> {

    @Query(value = "SELECT id FROM agreement_instance_reference_material ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String getLastId();

    List<AgreementInstanceReferenceMaterial> findByInstanceIdOrderBySortOrderAscTitleAsc(String instanceId);

    void deleteByInstanceId(String instanceId);

    Optional<AgreementInstanceReferenceMaterial> findByIdAndInstanceId(String id, String instanceId);
}
