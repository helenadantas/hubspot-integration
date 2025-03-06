package com.helena.hubspot.hubspotintegration.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class HubSpotAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;

    public HubSpotAuthenticationToken(String token, boolean authenticated) {
        super(null);
        this.token = token;
        setAuthenticated(authenticated);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }
}
