package com.autobuild.pipeline.auth.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

/**
 * Interface for extracting authorities from JWT tokens.
 * Allows different implementations for various identity providers.
 * @author Baibhab Dey
 */
public interface JwtAuthoritiesConverter extends Converter<Jwt, Collection<GrantedAuthority>> {
}