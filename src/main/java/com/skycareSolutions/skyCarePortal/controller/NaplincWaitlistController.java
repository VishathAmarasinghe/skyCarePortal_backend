package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.NaplincWaitlistRequestDTO;
import com.skycareSolutions.skyCarePortal.dto.NaplincWaitlistResponseDTO;
import com.skycareSolutions.skyCarePortal.model.NaplicWaitlist;
import com.skycareSolutions.skyCarePortal.repository.NaplicWaitlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public ResponseEntity<NaplincWaitlistResponseDTO> addToWaitlist(@RequestBody NaplincWaitlistRequestDTO request) {
        try {
            System.out.println("Received waitlist request: " + request);
            
            // Validate email
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                System.out.println("Email validation failed: email is null or empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NaplincWaitlistResponseDTO("Email is required", false, false));
            }

            // Normalize email to lowercase
            String normalizedEmail = request.getEmail().trim().toLowerCase();
            System.out.println("Normalized email: " + normalizedEmail);

            // Check if email already exists
            boolean exists = naplicWaitlistRepository.existsByEmail(normalizedEmail);
            System.out.println("Email exists check: " + exists);
            
            if (exists) {
                System.out.println("Email already exists in waitlist: " + normalizedEmail);
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
            System.out.println("Signup method: " + signupMethod);
            System.out.println("Name: " + request.getName());

            // Create and save waitlist entry
            NaplicWaitlist entry = new NaplicWaitlist(
                normalizedEmail,
                request.getName() != null ? request.getName().trim() : null,
                signupMethod
            );
            
            System.out.println("Creating waitlist entry: email=" + entry.getEmail() + ", name=" + entry.getName() + ", signupMethod=" + entry.getSignupMethod());
            
            NaplicWaitlist savedEntry = naplicWaitlistRepository.save(entry);
            
            System.out.println("Saved waitlist entry with ID: " + savedEntry.getId());
            System.out.println("Saved entry details: email=" + savedEntry.getEmail() + ", name=" + savedEntry.getName() + ", signupMethod=" + savedEntry.getSignupMethod() + ", joinedAt=" + savedEntry.getJoinedAt());
            
            // Verify the save by retrieving it
            NaplicWaitlist verifyEntry = naplicWaitlistRepository.findById(savedEntry.getId()).orElse(null);
            if (verifyEntry == null) {
                System.out.println("ERROR: Saved entry not found in database!");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NaplincWaitlistResponseDTO("Failed to save entry to database.", false, false));
            }
            System.out.println("Verified saved entry exists in database");

            return ResponseEntity.ok(new NaplincWaitlistResponseDTO(
                "You've been added to the waitlist!",
                true,
                false
            ));
        } catch (Exception e) {
            System.err.println("Error saving waitlist entry: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new NaplincWaitlistResponseDTO("Something went wrong. Please try again later. Error: " + e.getMessage(), false, false));
        }
    }

    @GetMapping
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> getAllWaitlistEntries() {
        try {
            System.out.println("Fetching all waitlist entries...");
            var allEntries = naplicWaitlistRepository.findAll();
            System.out.println("Found " + allEntries.size() + " waitlist entries");
            return ResponseEntity.ok(allEntries);
        } catch (Exception e) {
            System.err.println("Error fetching waitlist entries: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching waitlist entries: " + e.getMessage());
        }
    }

    @GetMapping("/{email}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> getWaitlistEntryByEmail(@PathVariable String email) {
        try {
            System.out.println("Fetching waitlist entry for email: " + email);
            var entry = naplicWaitlistRepository.findByEmail(email.toLowerCase());
            if (entry.isPresent()) {
                System.out.println("Found entry: " + entry.get());
                return ResponseEntity.ok(entry.get());
            } else {
                System.out.println("No entry found for email: " + email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No waitlist entry found for email: " + email);
            }
        } catch (Exception e) {
            System.err.println("Error fetching waitlist entry: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching waitlist entry: " + e.getMessage());
        }
    }
}

