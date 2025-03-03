package com.helena.hubspot.hubspotintegration.controller;

import com.helena.hubspot.hubspotintegration.dtos.CreateContactDTO;
import com.helena.hubspot.hubspotintegration.service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createContact(@RequestBody CreateContactDTO contactData,
                                                @RequestHeader("Authorization") String authToken) {
        return contactService.createContact(contactData, authToken);
    }
}