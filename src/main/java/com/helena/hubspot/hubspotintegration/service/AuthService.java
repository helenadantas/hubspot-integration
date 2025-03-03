package com.helena.hubspot.hubspotintegration.service;

import com.helena.hubspot.hubspotintegration.config.HubspotConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

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
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        return response.getBody();
    }
}
