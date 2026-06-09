package com.skycareSolutions.skyCarePortal.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class AgreementPdfService {

    private final RestTemplate restTemplate = new RestTemplate();

    public byte[] renderAgreementPdf(String html, String documentTitle) {
        String pdfServiceUrl = System.getenv().getOrDefault("PDF_SERVICE_URL", "http://localhost:3001");
        String endpoint = pdfServiceUrl + "/render/agreement";

        Map<String, Object> payload = new HashMap<>();
        payload.put("html", html);
        payload.put("documentTitle", documentTitle != null ? documentTitle : "Service Agreement");
        payload.put("generatedAtIso", Instant.now().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_PDF));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(endpoint, HttpMethod.POST, requestEntity, byte[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to generate agreement PDF. Status: " + response.getStatusCode());
    }
}
