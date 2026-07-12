package com.avnzor.oms_backend.auth.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

public final class JwtErrorMessages {

    private JwtErrorMessages() {
    }

    public static String resolve(JwtException exception) {
        if (exception instanceof ExpiredJwtException) {
            return "Access token has expired. Please log in again.";
        }
        if (exception instanceof SignatureException) {
            return "Invalid access token. Please log in again.";
        }
        if (exception instanceof MalformedJwtException) {
            return "Malformed access token.";
        }
        return "Invalid access token.";
    }
}
