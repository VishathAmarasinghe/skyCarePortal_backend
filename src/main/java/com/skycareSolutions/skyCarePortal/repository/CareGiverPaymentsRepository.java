package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.CareGiverPayments;
import com.skycareSolutions.skyCarePortal.model.CareGiverPaymentIDs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareGiverPaymentsRepository extends JpaRepository<CareGiverPayments, CareGiverPaymentIDs> {
}
