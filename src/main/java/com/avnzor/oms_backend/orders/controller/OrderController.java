package com.avnzor.oms_backend.orders.controller;

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
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management APIs")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    @GetMapping
    @PreAuthorize("@departmentAccess.isLogistic(authentication)")
    @Operation(summary = "List orders", description = "Returns orders. Restricted to the Logistic department.")
    public ResponseEntity<Map<String, Object>> listOrders() {
        return ResponseEntity.ok(Map.of(
                "message", "Orders placeholder",
                "items", List.of()
        ));
    }
}
