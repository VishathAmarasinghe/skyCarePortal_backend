package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.CareGiverDocuments;
import com.skycareSolutions.skyCarePortal.model.OtpRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpRecords, Long> {

    Optional<OtpRecords> findByEmailAndOtp(String email, String otp); // To validate OTP
}
