package com.helena.hubspot.hubspotintegration.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class HubSpotAuthenticationFilter extends OncePerRequestFilter {

    private final RestTemplate restTemplate;

    @Autowired
    public HubSpotAuthenticationFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        List<String> publicEndpoints = Arrays.asList("/oauth/authorize", "/oauth/callback", "/public/**",
                "/webhook/contact");

        for (String endpoint : publicEndpoints) {
            if (requestURI.startsWith(endpoint)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is missing or invalid");
            return;
        }

        String token = authHeader.replace("Bearer ", "");

        String hubSpotApiUrl = "https://api.hubapi.com/oauth/v1/access-tokens/" + token;
        ResponseEntity<Map> validationResponse = restTemplate.getForEntity(hubSpotApiUrl, Map.class);

        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid HubSpot token");
            return;
        }

        Map<String, Object> userDetails = validationResponse.getBody();
        String userEmail = (String) userDetails.get("user");
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userEmail, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}