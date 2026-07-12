package com.avnzor.oms_backend.audit.filter;

import com.avnzor.oms_backend.audit.dto.AuditLogEntry;
import com.avnzor.oms_backend.audit.service.AuditLogService;
import com.avnzor.oms_backend.audit.support.AuditPathResolver;
import com.avnzor.oms_backend.auth.security.WarehouseUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class AuditLoggingFilter extends OncePerRequestFilter {

    private final AuditLogService auditLogService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null || !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startedAt = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            auditLogService.logAsync(buildEntry(request, response, startedAt));
        }
    }

    private AuditLogEntry buildEntry(HttpServletRequest request, HttpServletResponse response, long startedAt) {
        AuditPathResolver.ResolvedPath resolvedPath = AuditPathResolver.resolve(request.getRequestURI());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("method", request.getMethod());
        details.put("path", request.getRequestURI());
        details.put("statusCode", response.getStatus());
        details.put("durationMs", System.currentTimeMillis() - startedAt);

        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isBlank()) {
            details.put("queryString", queryString);
        }

        String clientIp = resolveClientIp(request);
        if (clientIp != null) {
            details.put("clientIp", clientIp);
        }

        String actor = "anonymous";
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.getPrincipal() instanceof WarehouseUserPrincipal principal) {
            actor = principal.getUsername();
            details.put("userId", principal.getId());
            details.put("department", principal.getDepartment());
        }

        return new AuditLogEntry(
                AuditLogService.API_REQUEST_EVENT,
                resolvedPath.entityType(),
                resolvedPath.entityId(),
                actor,
                null,
                details
        );
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
