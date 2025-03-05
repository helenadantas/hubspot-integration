package com.helena.hubspot.hubspotintegration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helena.hubspot.hubspotintegration.dtos.CreateContactDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ContactService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private static final String HUBSPOT_API_URL = "https://api.hubapi.com/crm/v3/objects/contacts";

    public ContactService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String createContact(CreateContactDTO contactData, String authToken) {
        if (contactData == null) {
            throw new IllegalArgumentException("Contact data is required.");
        }

        if (authToken == null || authToken.isEmpty()) {
            throw new IllegalArgumentException("Authorization token is missing.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authToken);

        Map<String, Object> contactMap = objectMapper.convertValue(contactData, HashMap.class);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(contactMap, headers);

        ResponseEntity<String> response = restTemplate.exchange(HUBSPOT_API_URL, HttpMethod.POST, request, String.class);

        return response.getBody();
    }
}
