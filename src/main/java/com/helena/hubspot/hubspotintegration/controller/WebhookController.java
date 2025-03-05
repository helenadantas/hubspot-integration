package com.helena.hubspot.hubspotintegration.controller;

import com.helena.hubspot.hubspotintegration.dtos.WebhookContactDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @PostMapping("/contact")
    public ResponseEntity<String> receiveContactWebhook(@RequestBody List<WebhookContactDTO> payload) {
        System.out.println(payload);
        try {
            if (payload == null) {
                return ResponseEntity.badRequest().body("Payload invalid");
            }
            for (WebhookContactDTO webhook : payload) {
                System.out.println("Webhook received: " + webhook.getEventId());
            }
            return ResponseEntity.ok("Webhook received successfully!");
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