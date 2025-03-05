package com.helena.hubspot.hubspotintegration.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookContactDTO {
    private String event;
    private String contactId;
    private String email;
}
