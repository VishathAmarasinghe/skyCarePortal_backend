package com.skycareSolutions.skyCarePortal.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycareSolutions.skyCarePortal.dto.PlaceholderReferenceCategoryDTO;
import com.skycareSolutions.skyCarePortal.dto.PlaceholderReferenceItemDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class AgreementPlaceholderCatalogService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<PlaceholderReferenceCategoryDTO> cachedCatalog;

    public List<PlaceholderReferenceCategoryDTO> getReferenceCatalog() {
        if (cachedCatalog != null) {
            return cachedCatalog;
        }
        try (InputStream inputStream = new ClassPathResource("agreement-placeholder-catalog.json").getInputStream()) {
            List<PlaceholderReferenceCategoryDTO> raw = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<PlaceholderReferenceCategoryDTO>>() {}
            );
            cachedCatalog = raw.stream().map(category -> {
                PlaceholderReferenceCategoryDTO dto = new PlaceholderReferenceCategoryDTO();
                dto.setCategory(category.getCategory());
                List<PlaceholderReferenceItemDTO> items = category.getItems().stream().map(item -> {
                    PlaceholderReferenceItemDTO copy = new PlaceholderReferenceItemDTO();
                    copy.setToken(item.getToken());
                    copy.setCategory(category.getCategory());
                    copy.setDescription(item.getDescription());
                    copy.setExampleOutput(item.getExampleOutput());
                    copy.setSourceEntity(item.getSourceEntity());
                    return copy;
                }).collect(Collectors.toList());
                dto.setItems(items);
                return dto;
            }).collect(Collectors.toList());
            return cachedCatalog;
        } catch (Exception e) {
            throw new RuntimeException("Unable to load placeholder catalog", e);
        }
    }

    public List<PlaceholderReferenceItemDTO> getAllItems() {
        List<PlaceholderReferenceItemDTO> all = new ArrayList<>();
        for (PlaceholderReferenceCategoryDTO category : getReferenceCatalog()) {
            all.addAll(category.getItems());
        }
        return all;
    }

    public List<PlaceholderReferenceCategoryDTO> search(String query) {
        if (query == null || query.isBlank()) {
            return getReferenceCatalog();
        }
        String normalized = query.trim().toLowerCase(Locale.ROOT);
        return getReferenceCatalog().stream().map(category -> {
            PlaceholderReferenceCategoryDTO filtered = new PlaceholderReferenceCategoryDTO();
            filtered.setCategory(category.getCategory());
            filtered.setItems(category.getItems().stream()
                    .filter(item ->
                            item.getToken().toLowerCase(Locale.ROOT).contains(normalized)
                                    || item.getDescription().toLowerCase(Locale.ROOT).contains(normalized))
                    .collect(Collectors.toList()));
            return filtered;
        }).filter(category -> !category.getItems().isEmpty()).collect(Collectors.toList());
    }
}
