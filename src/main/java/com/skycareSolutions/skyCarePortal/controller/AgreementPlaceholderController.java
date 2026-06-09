package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.PlaceholderReferenceCategoryDTO;
import com.skycareSolutions.skyCarePortal.service.AgreementPlaceholderCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/agreement-placeholders")
public class AgreementPlaceholderController {

    @Autowired
    private AgreementPlaceholderCatalogService catalogService;

    @GetMapping("/reference")
    public ResponseEntity<List<PlaceholderReferenceCategoryDTO>> getReference(
            @RequestParam(value = "q", required = false) String query
    ) {
        if (query != null && !query.isBlank()) {
            return ResponseEntity.ok(catalogService.search(query));
        }
        return ResponseEntity.ok(catalogService.getReferenceCatalog());
    }
}
