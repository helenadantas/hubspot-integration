package com.helena.hubspot.hubspotintegration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final HubSpotAuthenticationFilter hubSpotAuthenticationFilter;

    public SecurityConfig(HubSpotAuthenticationFilter hubSpotAuthenticationFilter) {
        this.hubSpotAuthenticationFilter = hubSpotAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(a -> a.disable())
                .authorizeHttpRequests((auth) -> auth
                        .antMatchers(HttpMethod.GET, "/oauth/**").permitAll()
                        .antMatchers(HttpMethod.POST, "/webhook/contact").permitAll()
                        .antMatchers("/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(hubSpotAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}

