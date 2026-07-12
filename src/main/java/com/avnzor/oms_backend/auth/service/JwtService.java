package com.avnzor.oms_backend.auth.service;

import com.avnzor.oms_backend.auth.config.JwtProperties;
import com.avnzor.oms_backend.auth.security.WarehouseUserPrincipal;
import com.avnzor.oms_backend.tenants.security.PlatformUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    public static final String CLAIM_TOKEN_TYPE = "tokenType";
    public static final String TOKEN_TYPE_TENANT = "tenant";
    public static final String TOKEN_TYPE_PLATFORM = "platform";

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateTenantToken(WarehouseUserPrincipal principal) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtProperties.expirationMs());

        return Jwts.builder()
                .subject(principal.getUsername())
                .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_TENANT)
                .claim("userId", principal.getId())
                .claim("name", principal.getName())
                .claim("role", principal.getRole())
                .claim("department", principal.getDepartment())
                .claim("tenantId", principal.getTenantId())
                .claim("tenantSlug", principal.getTenantSlug())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public String generatePlatformToken(PlatformUserPrincipal principal) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtProperties.expirationMs());

        return Jwts.builder()
                .subject(principal.getUsername())
                .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_PLATFORM)
                .claim("userId", principal.getId())
                .claim("name", principal.getName())
                .claim("role", principal.getRole())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractTokenType(String token) {
        return parseClaims(token).get(CLAIM_TOKEN_TYPE, String.class);
    }

    public String extractTenantSlug(String token) {
        return parseClaims(token).get("tenantSlug", String.class);
    }

    public Long extractTenantId(String token) {
        Number tenantId = parseClaims(token).get("tenantId", Number.class);
        return tenantId != null ? tenantId.longValue() : null;
    }

    public boolean isTenantToken(String token) {
        return TOKEN_TYPE_TENANT.equals(extractTokenType(token));
    }

    public boolean isPlatformToken(String token) {
        return TOKEN_TYPE_PLATFORM.equals(extractTokenType(token));
    }

    public boolean isTenantTokenValid(String token, WarehouseUserPrincipal principal) {
        Claims claims = parseClaims(token);
        return TOKEN_TYPE_TENANT.equals(claims.get(CLAIM_TOKEN_TYPE, String.class))
                && principal.getUsername().equals(claims.getSubject())
                && principal.getTenantId().equals(claims.get("tenantId", Number.class).longValue())
                && principal.getTenantSlug().equals(claims.get("tenantSlug", String.class))
                && claims.getExpiration().after(new Date());
    }

    public boolean isPlatformTokenValid(String token, PlatformUserPrincipal principal) {
        Claims claims = parseClaims(token);
        return TOKEN_TYPE_PLATFORM.equals(claims.get(CLAIM_TOKEN_TYPE, String.class))
                && principal.getUsername().equals(claims.getSubject())
                && claims.getExpiration().after(new Date());
    }

    public long getExpirationSeconds() {
        return jwtProperties.expirationMs() / 1000;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
