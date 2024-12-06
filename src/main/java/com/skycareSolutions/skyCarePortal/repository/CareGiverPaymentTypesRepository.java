package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.CareGiverPaymentTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CareGiverPaymentTypesRepository extends JpaRepository<CareGiverPaymentTypes, String> {

    @Query(value = "SELECT dt.payment_typeid FROM caregiver_payment_types dt ORDER BY CAST(SUBSTRING(dt.payment_typeid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfPaymentType();
}
