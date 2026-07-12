package com.avnzor.oms_backend.auth.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtErrorMessages unit tests")
class JwtErrorMessagesTest {

    @Test
    @DisplayName("Given signature exception When resolving message Then returns login hint")
    void shouldResolveSignatureException() {
        assertThat(JwtErrorMessages.resolve(new SignatureException("bad signature")))
                .isEqualTo("Invalid access token. Please log in again.");
    }

    @Test
    @DisplayName("Given expired token When resolving message Then returns expiry hint")
    void shouldResolveExpiredJwtException() {
        assertThat(JwtErrorMessages.resolve(new ExpiredJwtException(null, null, "expired")))
                .isEqualTo("Access token has expired. Please log in again.");
    }

    @Test
    @DisplayName("Given malformed token When resolving message Then returns malformed hint")
    void shouldResolveMalformedJwtException() {
        assertThat(JwtErrorMessages.resolve(new MalformedJwtException("malformed")))
                .isEqualTo("Malformed access token.");
    }
}
