package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.LanguageDTO;
import com.skycareSolutions.skyCarePortal.model.Language;
import com.skycareSolutions.skyCarePortal.repository.LanguageRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LanguageService {
    @Autowired
    private LanguageRepository languageRepository;

    private LanguageDTO entityToDTO(Language language){
        LanguageDTO languageDTO = new LanguageDTO();
        languageDTO.setLanguage(language.getLanguage());
        languageDTO.setLanguageID(language.getLanguageID());
        languageDTO.setLanguageNotes(language.getLanguageNotes());
        return languageDTO;
    }

    private Language DTOtoEntity(LanguageDTO languageDTO){
        Language language = new Language();
        language.setLanguageNotes(languageDTO.getLanguageNotes());
        language.setLanguageID(languageDTO.getLanguageID());
        language.setLanguage(languageDTO.getLanguage());
        return language;
    }

    // Create or Save a Language
    public ResponseEntity<ApiResponse> createLanguage(LanguageDTO language) {
        String lastLanguageID = languageRepository.getLastIdOfLanguage();
        String newLanguageID = Micro_utils.idGenerator("language",lastLanguageID);
        language.setLanguageID(newLanguageID);
        Language OldLanguage = languageRepository.getlanguageIDByLanguageName(language.getLanguage());
        if (OldLanguage!=null){
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Language already created", false));
        }
        languageRepository.save(DTOtoEntity(language));
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Language Created Successfully ",true));
    }

    // Read or Get all Languages
    public List<LanguageDTO> getAllLanguages() {
        List<Language> lanuages = languageRepository.getAllLanguages();
        List<LanguageDTO> languageDTOS = new ArrayList<>();
        for (Language lang:lanuages){
            languageDTOS.add(entityToDTO(lang));
        }
        return languageDTOS;
    }

    // Read or Get a Language by ID
    public Optional<Language> getLanguageById(String languageID) {
        return languageRepository.findById(languageID);
    }

    // Update a Language
    public Language updateLanguage(String languageID, Language languageDetails) {
        return languageRepository.findById(languageID)
                .map(language -> {
                    language.setLanguage(languageDetails.getLanguage());
                    language.setLanguageNotes(languageDetails.getLanguageNotes());
                    return languageRepository.save(language);
                })
                .orElseThrow(() -> new RuntimeException("Language not found with ID " + languageID));
    }

    // Delete a Language
    public void deleteLanguage(String languageID) {
        languageRepository.deleteById(languageID);
    }

}
