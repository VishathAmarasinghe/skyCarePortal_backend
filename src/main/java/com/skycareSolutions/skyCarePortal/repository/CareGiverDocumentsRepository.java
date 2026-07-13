package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.CareGiver;
import com.skycareSolutions.skyCarePortal.model.CareGiverDocuments;
import com.skycareSolutions.skyCarePortal.model.CareGiverDocumentsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CareGiverDocumentsRepository extends JpaRepository<CareGiverDocuments, CareGiverDocumentsId> {
    // Custom query to get documents by caregiver ID
    @Query("SELECT d FROM CareGiverDocuments d WHERE d.careGiver = :careGiver")
    List<CareGiverDocuments> findByCareGiverID(CareGiver careGiver);

    @Query("SELECT d FROM CareGiverDocuments d WHERE d.id.careGiverID = :careGiverID")
    List<CareGiverDocuments> findByCareGiverId(@Param("careGiverID") String careGiverID);
}
