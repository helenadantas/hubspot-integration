package com.helena.hubspot.hubspotintegration.controller;

import com.helena.hubspot.hubspotintegration.dtos.WebhookContactDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/contact")
    public ResponseEntity<String> receiveContactWebhook(@RequestBody List<WebhookContactDTO> payload) {
        try {
            if (payload == null) {
                return ResponseEntity.badRequest().body("Payload invalid");
            }
            for (WebhookContactDTO webhook : payload) {
                logger.warn("Webhook received: " + webhook.getEventId());
            }
            return ResponseEntity.ok("Webhook received successfully!");
        } catch (IllegalArgumentException e) {
            logger.error("Error processing Webhook", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            logger.error("An internal error occurred: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred: "
                    + e.getMessage());
        }

    }
}