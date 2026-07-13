package com.avnzor.oms_backend.tracking.controller;

import com.avnzor.oms_backend.tracking.dto.TrackingListResponse;
import com.avnzor.oms_backend.tracking.service.TrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
@Tag(name = "Tracking", description = "Shipment tracking APIs")
@SecurityRequirement(name = "bearerAuth")
public class TrackingController {

    private final TrackingService trackingService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List tracking entries", description = "Returns tracking data for authenticated users.")
    public ResponseEntity<TrackingListResponse> listTracking() {
        return ResponseEntity.ok(trackingService.listTracking());
    }
}
