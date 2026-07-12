package com.avnzor.oms_backend.tracking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tracking")
@Tag(name = "Tracking", description = "Shipment tracking APIs")
@SecurityRequirement(name = "bearerAuth")
public class TrackingController {

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List tracking entries", description = "Returns tracking data for authenticated users.")
    public ResponseEntity<Map<String, Object>> listTracking() {
        return ResponseEntity.ok(Map.of(
                "message", "Tracking placeholder",
                "items", List.of()
        ));
    }
}
