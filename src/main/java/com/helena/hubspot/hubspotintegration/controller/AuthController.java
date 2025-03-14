package com.helena.hubspot.hubspotintegration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.helena.hubspot.hubspotintegration.config.HubspotConfig;
import com.helena.hubspot.hubspotintegration.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/oauth")
public class AuthController {


    private final HubspotConfig hubSpotConfig;
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    public AuthController(HubspotConfig hubSpotConfig, AuthService authService) {
        this.hubSpotConfig = hubSpotConfig;
        this.authService = authService;
    }


    @GetMapping("/authorize")
    public ResponseEntity<String> getAuthorizationUrl() {
        try {
            return ResponseEntity.ok(authService.generateAuthorizationUrl());
        } catch (Exception e) {
            logger.error("Error processing OAuth callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating authorization URL: " + e.getMessage());
        }
    }

    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> handleOAuthCallback(@RequestParam("code") String code) {

        try {
            if (code == null || code.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Invalid code, please authenticate again."));
            }
            String response = authService.exchangeCodeForToken(code);
            Map<String, String> tokenData = new HashMap<>();

            if (response.startsWith("{")) {
                ObjectMapper objectMapper = new ObjectMapper();
                tokenData = objectMapper.readValue(response, new TypeReference<Map<String, String>>() {});
            } else {
                tokenData.put("access_token", response);
            }            return ResponseEntity.ok(tokenData);

        } catch (HttpClientErrorException.BadRequest e) {
            logger.warn("Invalid code provided", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Invalid code, please authenticate again."));
        } catch (JsonProcessingException e) {
            logger.error("Error processing response from HubSpot", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error processing response from HubSpot"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error generating authorization URL: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error generating authorization URL: " + e.getMessage()));
        }

    }


}
