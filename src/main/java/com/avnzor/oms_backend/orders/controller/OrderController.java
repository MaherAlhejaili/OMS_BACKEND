package com.avnzor.oms_backend.orders.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @GetMapping
    @PreAuthorize("@departmentAccess.isLogistic(authentication)")
    public ResponseEntity<Map<String, Object>> listOrders() {
        return ResponseEntity.ok(Map.of(
                "message", "Orders placeholder",
                "items", List.of()
        ));
    }
}
