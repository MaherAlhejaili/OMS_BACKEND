package com.avnzor.oms_backend.sales.controller;

import com.avnzor.oms_backend.sales.dto.EditSaleRequest;
import com.avnzor.oms_backend.sales.dto.PagedSaleListResponse;
import com.avnzor.oms_backend.sales.dto.SaleDetailResponse;
import com.avnzor.oms_backend.sales.dto.UpdateSaleStatusRequest;
import com.avnzor.oms_backend.sales.dto.UpdateSaleStatusResponse;
import com.avnzor.oms_backend.sales.dto.UpdateShelvingStatusRequest;
import com.avnzor.oms_backend.sales.dto.UpdateShelvingStatusResponse;
import com.avnzor.oms_backend.sales.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Sale management APIs")
@SecurityRequirement(name = "bearerAuth")
public class SaleController {

    private final SaleService saleService;

    @GetMapping
    @PreAuthorize("@departmentAccess.isLogistic(authentication)")
    @Operation(summary = "List sales", description = "Returns paginated sales. Restricted to the Logistic department.")
    public ResponseEntity<PagedSaleListResponse> getAllSales(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "DESC") String sortOrder,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(saleService.getAllSales(page, limit, sortBy, sortOrder, status, search));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@departmentAccess.isLogistic(authentication)")
    @Operation(summary = "Get sale by ID with batches")
    public ResponseEntity<SaleDetailResponse> getSaleById(@PathVariable Integer id) {
        return ResponseEntity.ok(saleService.getSaleWithBatches(id));
    }

    @GetMapping("/lookup")
    @PreAuthorize("@departmentAccess.isLogistic(authentication)")
    @Operation(summary = "Get sale by id, reference_no, or tracking_id")
    public ResponseEntity<SaleDetailResponse> getSale(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String referenceNo,
            @RequestParam(required = false) String trackingId
    ) {
        return ResponseEntity.ok(saleService.getSale(id, referenceNo, trackingId));
    }

    @PatchMapping({"/{id}", "/{id}/status"})
    @PreAuthorize("@departmentAccess.isLogistic(authentication)")
    @Operation(summary = "Update sale status", description = "Accepts PATCH on /{id} or /{id}/status")
    public ResponseEntity<UpdateSaleStatusResponse> updateSaleStatus(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateSaleStatusRequest request
    ) {
        return ResponseEntity.ok(saleService.updateSaleStatus(id, request));
    }

    @PatchMapping("/shelving-items/{shelvingItemId}/status")
    @PreAuthorize("@departmentAccess.isLogistic(authentication)")
    @Operation(summary = "Update shelving item status")
    public ResponseEntity<UpdateShelvingStatusResponse> updateShelvingStatus(
            @PathVariable Integer shelvingItemId,
            @Valid @RequestBody UpdateShelvingStatusRequest request
    ) {
        return ResponseEntity.ok(saleService.updateShelvingStatus(shelvingItemId, request.status()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@departmentAccess.isLogistic(authentication)")
    @Operation(summary = "Edit sale shipping details and items")
    public ResponseEntity<SaleDetailResponse> editSale(
            @PathVariable Integer id,
            @Valid @RequestBody EditSaleRequest request
    ) {
        return ResponseEntity.ok(saleService.editSale(id, request));
    }
}
