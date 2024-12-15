package com.skycareSolutions.skyCarePortal.scheduler;

import com.skycareSolutions.skyCarePortal.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OtpCleanupScheduler {

    @Autowired
    private OtpRepository otpRepository;

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredOtps() {
        otpRepository.deleteAll(otpRepository.findAll().stream()
                .filter(otp -> otp.getExpiryTime().isBefore(LocalDateTime.now()))
                .toList());
    }
}
