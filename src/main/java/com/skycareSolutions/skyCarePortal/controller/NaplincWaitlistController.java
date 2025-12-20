package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.NaplincWaitlistRequestDTO;
import com.skycareSolutions.skyCarePortal.dto.NaplincWaitlistResponseDTO;
import com.skycareSolutions.skyCarePortal.model.NaplicWaitlist;
import com.skycareSolutions.skyCarePortal.repository.NaplicWaitlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/naplinc/waitlist")
public class NaplincWaitlistController {

    @Autowired
    private NaplicWaitlistRepository naplicWaitlistRepository;

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok()
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
            .header("Access-Control-Max-Age", "3600")
            .build();
    }

    @PostMapping
    @CrossOrigin(origins = "*", maxAge = 3600)
    public ResponseEntity<NaplincWaitlistResponseDTO> addToWaitlist(@RequestBody NaplincWaitlistRequestDTO request) {
        try {
            // Validate email
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NaplincWaitlistResponseDTO("Email is required", false, false));
            }

            // Normalize email to lowercase
            String normalizedEmail = request.getEmail().trim().toLowerCase();

            // Check if email already exists
            if (naplicWaitlistRepository.existsByEmail(normalizedEmail)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new NaplincWaitlistResponseDTO("This email is already on the waitlist.", false, true));
            }

            // Validate signup method
            String signupMethod = request.getSignupMethod();
            if (signupMethod == null || signupMethod.trim().isEmpty()) {
                signupMethod = "EMAIL";
            } else {
                signupMethod = signupMethod.toUpperCase();
                if (!signupMethod.equals("GOOGLE") && !signupMethod.equals("EMAIL")) {
                    signupMethod = "EMAIL";
                }
            }

            // Create and save waitlist entry
            NaplicWaitlist entry = new NaplicWaitlist(
                normalizedEmail,
                request.getName() != null ? request.getName().trim() : null,
                signupMethod
            );
            naplicWaitlistRepository.save(entry);

            return ResponseEntity.ok(new NaplincWaitlistResponseDTO(
                "You've been added to the waitlist!",
                true,
                false
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new NaplincWaitlistResponseDTO("Something went wrong. Please try again later.", false, false));
        }
    }
}

