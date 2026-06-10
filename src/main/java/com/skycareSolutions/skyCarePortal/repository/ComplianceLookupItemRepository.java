package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.ComplianceLookupItem;
import com.skycareSolutions.skyCarePortal.model.ComplianceLookupType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplianceLookupItemRepository extends JpaRepository<ComplianceLookupItem, String> {

    List<ComplianceLookupItem> findByLookupTypeOrderBySortOrderAscTitleAsc(ComplianceLookupType lookupType);

    long countByLookupType(ComplianceLookupType lookupType);

    @Query(value = "SELECT c.lookupid FROM compliance_lookup c ORDER BY CAST(SUBSTRING(c.lookupid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastLookupId();
}
