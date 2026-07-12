package com.avnzor.oms_backend.sales.support;

public final class ProductCodeNormalizer {

    private ProductCodeNormalizer() {
    }

    public static String normalize(String code) {
        if (code == null) {
            return "0";
        }
        String trimmed = code.replaceFirst("^0+", "");
        return trimmed.isEmpty() ? "0" : trimmed;
    }
}
