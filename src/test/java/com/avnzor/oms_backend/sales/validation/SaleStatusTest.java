package com.avnzor.oms_backend.sales.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SaleStatus unit tests")
class SaleStatusTest {

    @Test
    @DisplayName("Given fulfilled status When resolving job type Then returns shipping")
    void shouldResolveShippingJobType() {
        assertThat(SaleStatus.resolveJobType("fulfilled")).contains("shipping");
    }

    @Test
    @DisplayName("Given unknown status When mapping display name Then returns raw value")
    void shouldFallbackToRawStatus() {
        assertThat(SaleStatus.toDisplayName("custom_status")).isEqualTo("custom_status");
    }
}
