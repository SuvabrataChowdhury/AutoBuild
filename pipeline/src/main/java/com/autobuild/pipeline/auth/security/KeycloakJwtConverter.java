package com.autobuild.pipeline.auth.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Extracts roles and authorities from Keycloak JWT claims.
 * Supports both realm_access and resource_access role structures.
 * @author Baibhab Dey
 */
@Component
public class KeycloakJwtConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Extract realm-level roles
        extractRealmRoles(jwt, authorities);
        
        // Extract client-level roles
        extractClientRoles(jwt, authorities);

        return authorities;
    }

    @SuppressWarnings("unchecked")
    private void extractRealmRoles(Jwt jwt, List<GrantedAuthority> authorities) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            List<String> roles = (List<String>) realmAccess.get("roles");
            authorities.addAll(roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList()));
        }
    }

    @SuppressWarnings("unchecked")
    private void extractClientRoles(Jwt jwt, List<GrantedAuthority> authorities) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            resourceAccess.values().forEach(clientData -> {
                if (clientData instanceof Map) {
                    Map<String, Object> clientMap = (Map<String, Object>) clientData;
                    if (clientMap.containsKey("roles")) {
                        List<String> roles = (List<String>) clientMap.get("roles");
                        authorities.addAll(roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            .collect(Collectors.toList()));
                    }
                }
            });
        }
    }
}