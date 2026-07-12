package com.avnzor.oms_backend.sales.validation;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class SaleStatus {

    private static final Map<String, String> DISPLAY_NAMES = Map.ofEntries(
            Map.entry("PENDING", "pending"),
            Map.entry("FULFILLED", "fulfilled"),
            Map.entry("SHIPPED", "shipped"),
            Map.entry("CANCELLED", "cancelled"),
            Map.entry("RETURNED", "returned"),
            Map.entry("PROCESSING", "processing"),
            Map.entry("ON_HOLD", "on_hold")
    );

    private SaleStatus() {
    }

    public static String toDisplayName(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return "pending";
        }
        String key = rawStatus.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        return DISPLAY_NAMES.getOrDefault(key, rawStatus);
    }

    public static Optional<String> resolveJobType(String saleStatus) {
        if (saleStatus == null) {
            return Optional.empty();
        }
        return switch (saleStatus.toLowerCase(Locale.ROOT)) {
            case "fulfilled", "shipped" -> Optional.of("shipping");
            case "processing", "pending" -> Optional.of("picking");
            case "cancelled", "returned" -> Optional.of("return");
            default -> Optional.of("");
        };
    }
}
