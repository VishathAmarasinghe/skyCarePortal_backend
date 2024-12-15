package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.model.CareGiverDocuments;
import com.skycareSolutions.skyCarePortal.model.OtpRecords;
import com.skycareSolutions.skyCarePortal.repository.OtpRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    // Generate and store OTP
    public String generateAndStoreOtp(String email) {
        String otp = Micro_utils.generateOtp(6); // Custom OTP generator
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        OtpRecords otpRecord = new OtpRecords();
        otpRecord.setEmail(email);
        otpRecord.setOtp(otp);
        otpRecord.setExpiryTime(expiryTime);

        otpRepository.save(otpRecord);
        return otp;
    }

    // Validate OTP
    public boolean validateOtp(String email, String otp) {
        Optional<OtpRecords> otpRecordOpt = otpRepository.findByEmailAndOtp(email, otp);

        if (otpRecordOpt.isPresent()) {
            OtpRecords otpRecord = otpRecordOpt.get();

            if (otpRecord.getExpiryTime().isAfter(LocalDateTime.now()) && !otpRecord.isUsed()) {
                otpRecord.setUsed(true); // Mark as used
                otpRepository.save(otpRecord); // Update in DB
                return true;
            }
        }

        return false;
    }
}
