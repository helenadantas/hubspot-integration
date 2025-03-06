package com.helena.hubspot.hubspotintegration.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helena.hubspot.hubspotintegration.config.HubspotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final HubspotConfig hubSpotConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public AuthService(HubspotConfig hubSpotConfig) {
        this.hubSpotConfig = hubSpotConfig;
    }

    public String generateAuthorizationUrl() {
        return hubSpotConfig.getAuthUrl() +
                "?client_id=" + hubSpotConfig.getClientId() +
                "&redirect_uri=" + hubSpotConfig.getRedirectUri() +
                "&scope=crm.objects.contacts.read%20crm.objects.contacts.write";
    }

    public String exchangeCodeForToken(String code) {
        String url = hubSpotConfig.getTokenUrl();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", hubSpotConfig.getClientId());
        body.add("client_secret", hubSpotConfig.getClientSecret());
        body.add("redirect_uri", hubSpotConfig.getRedirectUri());
        body.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getBody() == null || response.getBody().isEmpty()) {
                logger.error("Received empty response from HubSpot OAuth API");
                throw new RuntimeException("Empty response from authentication server");
            }

            String responseBody = response.getBody().trim();
            logger.info("Response from HubSpot: " + responseBody);

            if (responseBody.startsWith("{")) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);

                    if (jsonNode.has("access_token")) {
                        return jsonNode.get("access_token").asText();
                    } else {
                        throw new RuntimeException("Invalid JSON response: 'access_token' not found");
                    }
                } catch (JsonProcessingException e) {
                    logger.error("Error parsing JSON response", e);
                    throw new RuntimeException("Error parsing authentication response");
                }
            } else {
                logger.warn("Received non-JSON response, assuming it's a token.");
                return responseBody;
            }

        } catch (HttpClientErrorException.BadRequest e) {
            logger.error("Invalid authorization code: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Invalid authorization code. Please authenticate again.");
        } catch (Exception e) {
            logger.error("Error exchanging code for token", e);
            throw new RuntimeException("Error exchanging code for token: " + e.getMessage());
        }
    }
}
