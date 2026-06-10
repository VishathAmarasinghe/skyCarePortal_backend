package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.HomeMessageSettingDTO;
import com.skycareSolutions.skyCarePortal.service.HomeMessageSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/settings/home-message")
public class HomeMessageSettingController {

    @Autowired
    private HomeMessageSettingService service;

    @GetMapping
    public ResponseEntity<HomeMessageSettingDTO> get() {
        return ResponseEntity.ok(service.get());
    }

    @PutMapping
    public ResponseEntity<HomeMessageSettingDTO> update(@RequestBody HomeMessageSettingDTO input) {
        return ResponseEntity.ok(service.update(input));
    }
}

