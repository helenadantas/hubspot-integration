package com.helena.hubspot.hubspotintegration.controller;

import com.helena.hubspot.hubspotintegration.dtos.WebhookContactDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @PostMapping("/contact")
    public ResponseEntity<String> receiveContactWebhook(@RequestBody WebhookContactDTO payload) {
        try {
            if (payload == null) {
                return ResponseEntity.badRequest().body("Payload invalid");
            }
            return ResponseEntity.ok("Webhook received successfully! New contact created: " + payload.getEmail());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred: "
                    + e.getMessage());
        }

    }
}