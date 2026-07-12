package com.avnzor.oms_backend.tracking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tracking")
public class TrackingController {

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> listTracking() {
        return ResponseEntity.ok(Map.of(
                "message", "Tracking placeholder",
                "items", List.of()
        ));
    }
}
