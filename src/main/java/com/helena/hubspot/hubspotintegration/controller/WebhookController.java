package com.helena.hubspot.hubspotintegration.controller;

import com.helena.hubspot.hubspotintegration.dtos.WebhookContactDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @PostMapping("/contact")
    public ResponseEntity<String> handleContactWebhook(@RequestBody WebhookContactDTO payload) {
        return ResponseEntity.ok("Webhook received successfully! New contact created: " + payload);
    }
}