package com.avnzor.oms_backend.sales.support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductCodeNormalizer unit tests")
class ProductCodeNormalizerTest {

    @Test
    @DisplayName("Given leading zeros When normalizing Then strips zeros")
    void shouldStripLeadingZeros() {
        assertThat(ProductCodeNormalizer.normalize("000123")).isEqualTo("123");
    }

    @Test
    @DisplayName("Given all zeros When normalizing Then returns zero")
    void shouldReturnZeroForAllZeros() {
        assertThat(ProductCodeNormalizer.normalize("000")).isEqualTo("0");
    }

    @Test
    @DisplayName("Given null When normalizing Then returns zero")
    void shouldHandleNull() {
        assertThat(ProductCodeNormalizer.normalize(null)).isEqualTo("0");
    }
}
