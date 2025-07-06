package com.skycareSolutions.skyCarePortal.controller;


import com.skycareSolutions.skyCarePortal.dto.CareGiverDTO;
import com.skycareSolutions.skyCarePortal.model.WaitList;
import com.skycareSolutions.skyCarePortal.repository.WaitListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/waitlist")
public class WaitingListController {


    @Autowired
    private WaitListRepository waitListRepository;

    @PostMapping
    public ResponseEntity<String> addEmailToWaitlist(@RequestParam("email") String email) {
        try {
            if (waitListRepository.existsByEmail(email)) {
                return ResponseEntity.status(409).body("This email is already on the waitlist.");
            }
            WaitList entry = new WaitList(email);
            waitListRepository.save(entry);
            return ResponseEntity.ok("You've been added to the waitlist!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Something went wrong.");
        }
    }
}
