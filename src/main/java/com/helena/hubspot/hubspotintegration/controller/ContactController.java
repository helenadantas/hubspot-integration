package com.helena.hubspot.hubspotintegration.controller;

import com.helena.hubspot.hubspotintegration.dtos.CreateContactDTO;
import com.helena.hubspot.hubspotintegration.service.ContactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService contactService;

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
            return ResponseEntity.badRequest().body("Bad Request: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error: " + e.getMessage());
        }
    }
}