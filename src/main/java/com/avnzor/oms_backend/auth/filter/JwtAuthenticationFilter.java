package com.avnzor.oms_backend.auth.filter;

import com.avnzor.oms_backend.auth.security.SecurityProblemSupport;
import com.avnzor.oms_backend.auth.security.WarehouseUserPrincipal;
import com.avnzor.oms_backend.auth.service.JwtService;
import com.avnzor.oms_backend.auth.util.JwtErrorMessages;
import io.jsonwebtoken.JwtException;
import com.avnzor.oms_backend.auth.service.WarehouseUserDetailsService;
import com.avnzor.oms_backend.tenants.resolver.TenantResolver;
import com.avnzor.oms_backend.tenants.security.PlatformUserPrincipal;
import com.avnzor.oms_backend.tenants.service.PlatformUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final WarehouseUserDetailsService warehouseUserDetailsService;
    private final PlatformUserDetailsService platformUserDetailsService;
    private final TenantResolver tenantResolver;
    private final SecurityProblemSupport securityProblemSupport;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            WarehouseUserDetailsService warehouseUserDetailsService,
            PlatformUserDetailsService platformUserDetailsService,
            TenantResolver tenantResolver,
            SecurityProblemSupport securityProblemSupport
    ) {
        this.jwtService = jwtService;
        this.warehouseUserDetailsService = warehouseUserDetailsService;
        this.platformUserDetailsService = platformUserDetailsService;
        this.tenantResolver = tenantResolver;
        this.securityProblemSupport = securityProblemSupport;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authorizationHeader.substring(7);

        try {
            String username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateToken(jwt, username, request);
            }

            filterChain.doFilter(request, response);
        } catch (JwtException exception) {
            log.debug("Rejected invalid JWT on {}: {}", request.getRequestURI(), exception.getMessage());
            securityProblemSupport.writeUnauthorized(
                    request,
                    response,
                    JwtErrorMessages.resolve(exception)
            );
        }
    }

    private void authenticateToken(String jwt, String username, HttpServletRequest request) {
        if (jwtService.isPlatformToken(jwt)) {
            authenticatePlatformToken(jwt, username, request);
            return;
        }

        if (jwtService.isTenantToken(jwt)) {
            authenticateTenantToken(jwt, username, request);
        }
    }

    private void authenticatePlatformToken(String jwt, String username, HttpServletRequest request) {
        UserDetails userDetails = platformUserDetailsService.loadUserByUsername(username);

        if (userDetails instanceof PlatformUserPrincipal principal
                && jwtService.isPlatformTokenValid(jwt, principal)) {
            setAuthentication(userDetails, request);
        }
    }

    private void authenticateTenantToken(String jwt, String username, HttpServletRequest request) {
        tenantResolver.resolveFromJwt(jwt);
        UserDetails userDetails = warehouseUserDetailsService.loadUserByUsername(username);

        if (userDetails instanceof WarehouseUserPrincipal principal
                && jwtService.isTenantTokenValid(jwt, principal)) {
            setAuthentication(userDetails, request);
        }
    }

    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
