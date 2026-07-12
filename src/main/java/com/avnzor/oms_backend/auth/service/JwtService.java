package com.avnzor.oms_backend.auth.service;

import com.avnzor.oms_backend.auth.config.JwtProperties;
import com.avnzor.oms_backend.auth.security.WarehouseUserPrincipal;
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

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(WarehouseUserPrincipal principal) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtProperties.expirationMs());

        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("userId", principal.getId())
                .claim("name", principal.getName())
                .claim("role", principal.getRole())
                .claim("department", principal.getDepartment())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, WarehouseUserPrincipal principal) {
        Claims claims = parseClaims(token);
        String username = claims.getSubject();
        Date expiration = claims.getExpiration();

        return username.equals(principal.getUsername()) && expiration.after(new Date());
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
