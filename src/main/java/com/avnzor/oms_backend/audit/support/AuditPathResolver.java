package com.avnzor.oms_backend.audit.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AuditPathResolver {

    private static final Pattern API_PATH = Pattern.compile("^/api/v1/([^/]+)(?:/([^/]+))?");

    private AuditPathResolver() {
    }

    public static ResolvedPath resolve(String requestPath) {
        if (requestPath == null || requestPath.isBlank()) {
            return new ResolvedPath("api", null);
        }

        Matcher matcher = API_PATH.matcher(requestPath);
        if (!matcher.find()) {
            return new ResolvedPath("api", null);
        }

        String entityType = matcher.group(1);
        String entityId = matcher.group(2);
        return new ResolvedPath(entityType, entityId);
    }

    public record ResolvedPath(String entityType, String entityId) {
    }
}
