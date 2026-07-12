package com.avnzor.oms_backend.sales.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PagedSaleListResponse(
        List<SaleSummaryResponse> data,
        long total,
        int page,
        int limit,
        int totalPages
) {
}
