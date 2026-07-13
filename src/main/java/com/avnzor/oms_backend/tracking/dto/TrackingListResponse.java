package com.avnzor.oms_backend.tracking.dto;

import java.util.List;

public record TrackingListResponse(
        String message,
        List<Object> items
) {
}
