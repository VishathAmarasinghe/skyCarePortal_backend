package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.CareGiverPayments;
import com.skycareSolutions.skyCarePortal.model.CareGiverPaymentIDs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CareGiverPaymentsRepository extends JpaRepository<CareGiverPayments, CareGiverPaymentIDs> {

    @Query("SELECT p FROM CareGiverPayments p WHERE p.id.careGiverID = :careGiverId")
    List<CareGiverPayments> findByCareGiverId(@Param("careGiverId") String careGiverId);
}
