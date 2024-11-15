package com.skycareSolutions.skyCarePortal.controller;


import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.LanguageDTO;
import com.skycareSolutions.skyCarePortal.model.Language;
import com.skycareSolutions.skyCarePortal.service.LanguageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/languages")
public class LanguageController {
    private static final Logger logger = LoggerFactory.getLogger(com.skycareSolutions.skyCarePortal.service.LanguageService.class);

    @Autowired
    private LanguageService languageService;

    // Create a new Language
    @PostMapping
    public ResponseEntity<ApiResponse> createLanguage(@RequestBody LanguageDTO language) {
        return languageService.createLanguage(language);
    }

    // Get all Languages
    @GetMapping
    public ResponseEntity<List<LanguageDTO>> getAllLanguages() {
        List<LanguageDTO> languages = languageService.getAllLanguages();
        return ResponseEntity.ok(languages);
    }

    // Get Language by ID
    @GetMapping("/{id}")
    public ResponseEntity<Language> getLanguageById(@PathVariable String id) {
        return languageService.getLanguageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update Language
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateLanguage(@PathVariable String id, @RequestBody Language languageDetails) {
        try {
            Language updatedLanguage = languageService.updateLanguage(id, languageDetails);
            return ResponseEntity.ok(new ApiResponse("Updated successfully",true));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete Language
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLanguage(@PathVariable String id) {
        languageService.deleteLanguage(id);
        return ResponseEntity.noContent().build();
    }

}
