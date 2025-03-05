package com.helena.hubspot.hubspotintegration.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookContactDTO {
    private int appId;
    private int eventId;
    private int subscriptionId;
    private int portalId;
    private long occurredAt;
    private String subscriptionType;
    private int attemptNumber;
    private int objectId;
    private String changeSource;
    private String changeFlag;
}
