package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.CarePlanDTO;
import com.skycareSolutions.skyCarePortal.dto.NdisSupportPlanPdfRequestDTO;
import com.skycareSolutions.skyCarePortal.model.CarePlan;
import com.skycareSolutions.skyCarePortal.repository.CarePlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
public class NdisSupportPlanPdfService {

    @Autowired
    private CarePlanRepository carePlanRepository;

    @Autowired
    private CarePlanService carePlanService;

    private final RestTemplate restTemplate = new RestTemplate();

    public byte[] generateNdisSupportPlanPdfBytes(String carePlanId) {
        CarePlan carePlan = carePlanRepository.findById(carePlanId)
                .orElseThrow(() -> new RuntimeException("CarePlan not found with ID: " + carePlanId));

        CarePlanDTO carePlanDTO = carePlanService.getCarePlansByCarePlanID(carePlanId);

        NdisSupportPlanPdfRequestDTO payload = new NdisSupportPlanPdfRequestDTO();

        NdisSupportPlanPdfRequestDTO.CompanyDTO company = new NdisSupportPlanPdfRequestDTO.CompanyDTO();
        company.setName(System.getenv().getOrDefault("SKYCARE_COMPANY_NAME", "Skycare Solutions"));
        company.setLogoDataUrl("");
        payload.setCompany(company);

        NdisSupportPlanPdfRequestDTO.ClientNameDTO client = new NdisSupportPlanPdfRequestDTO.ClientNameDTO();
        client.setFirstName(carePlan.getClientID().getFirstName());
        client.setLastName(carePlan.getClientID().getLastName());
        client.setPreferredName(carePlan.getClientID().getPreferredName());
        payload.setClient(client);

        payload.setCarePlan(carePlanDTO);
        payload.setGeneratedAtIso(Instant.now().toString());

        String pdfServiceUrl = System.getenv().getOrDefault("PDF_SERVICE_URL", "http://localhost:3001");
        String endpoint = pdfServiceUrl + "/render/ndis-support-plan";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_PDF));

        HttpEntity<NdisSupportPlanPdfRequestDTO> requestEntity = new HttpEntity<>(payload, headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(endpoint, HttpMethod.POST, requestEntity, byte[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new RuntimeException("Failed to generate PDF. Status: " + response.getStatusCode());
    }
}

