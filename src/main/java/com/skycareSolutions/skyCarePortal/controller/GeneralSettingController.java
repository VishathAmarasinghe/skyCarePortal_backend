package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.GeneralSettingDTO;
import com.skycareSolutions.skyCarePortal.service.GeneralSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/settings/general")
public class GeneralSettingController {

    @Autowired
    private GeneralSettingService service;

    @GetMapping
    public ResponseEntity<GeneralSettingDTO> get() {
        return ResponseEntity.ok(service.get());
    }

    @PutMapping
    public ResponseEntity<GeneralSettingDTO> update(@RequestBody GeneralSettingDTO input) {
        return ResponseEntity.ok(service.update(input));
    }
}
