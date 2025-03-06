package com.helena.hubspot.hubspotintegration.controller;

import com.helena.hubspot.hubspotintegration.dtos.CreateContactDTO;
import com.helena.hubspot.hubspotintegration.service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService contactService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createContact(
            @RequestBody(required = false) CreateContactDTO contactData,
            @RequestHeader(value = "Authorization", required = false) String authToken) {
        try {
            if (contactData == null) {
                return ResponseEntity.badRequest().body("Contact data can not be empty.");
            }

            if (authToken == null || authToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Unauthorized: The token is missing.");
            }

            String response = contactService.createContact(contactData, authToken);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Error processing Contact: " + e.getMessage());
            return ResponseEntity.badRequest().body("Bad Request: " + e.getMessage());
        } catch (HttpClientErrorException.Conflict e) {
            String responseBody = e.getResponseBodyAsString();
            String existingContactId = contactService.extractExistingContactId(responseBody);
            logger.error("Contact already exists. Existing ID: " + existingContactId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Contact already exists. Existing ID: " + existingContactId);
        } catch (Exception e) {
            logger.error("Error processing Webhook", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error: " + e.getMessage());
        }
    }
}