package com.avnzor.oms_backend.sales.service;

import com.avnzor.oms_backend.common.exception.ResourceNotFoundException;
import com.avnzor.oms_backend.sales.dto.UpdateSaleStatusRequest;
import com.avnzor.oms_backend.sales.dto.UpdateSaleStatusResponse;
import com.avnzor.oms_backend.sales.dto.UpdateShelvingStatusResponse;
import com.avnzor.oms_backend.sales.entity.Sale;
import com.avnzor.oms_backend.sales.entity.SaleStatusHistory;
import com.avnzor.oms_backend.sales.mapper.SaleMapper;
import com.avnzor.oms_backend.sales.repository.SaleItemRepository;
import com.avnzor.oms_backend.sales.repository.SaleRepository;
import com.avnzor.oms_backend.sales.repository.SaleStatusHistoryRepository;
import com.avnzor.oms_backend.sales.support.SaleBatchSupport;
import com.avnzor.oms_backend.sales.support.SaleQuerySupport;
import com.avnzor.oms_backend.sales.support.SaleRelatedDataSupport;
import com.avnzor.oms_backend.sales.support.SaleStockSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaleService unit tests")
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SaleItemRepository saleItemRepository;

    @Mock
    private SaleStatusHistoryRepository saleStatusHistoryRepository;

    @Mock
    private SaleQuerySupport saleQuerySupport;

    @Mock
    private SaleStockSupport saleStockSupport;

    @Mock
    private SaleBatchSupport saleBatchSupport;

    @Mock
    private SaleRelatedDataSupport saleRelatedDataSupport;

    @Mock
    private SaleMapper saleMapper;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private SaleService saleService;

    @BeforeEach
    void setUp() {
        saleService = new SaleService(
                saleRepository,
                saleItemRepository,
                saleStatusHistoryRepository,
                saleQuerySupport,
                saleStockSupport,
                saleBatchSupport,
                saleRelatedDataSupport,
                saleMapper,
                jdbcTemplate
        );
    }

    @Test
    @DisplayName("Given missing sale When updating status Then throws not found")
    void shouldRejectMissingSaleOnStatusUpdate() {
        when(saleRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saleService.updateSaleStatus(99, new UpdateSaleStatusRequest(
                "shipped", null, null, null, null, null
        ))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Given valid status update When sale status changes Then records history and returns response")
    void shouldUpdateSaleStatusAndRecordHistory() {
        Sale sale = new Sale();
        sale.setId(1);
        sale.setSaleStatus("pending");
        sale.setPaymentStatus("pending");
        sale.setJobType("picking");

        when(saleRepository.findById(1)).thenReturn(Optional.of(sale));
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(saleMapper.toStatusUpdateResponse(any(Sale.class))).thenAnswer(invocation -> {
            Sale saved = invocation.getArgument(0);
            return new UpdateSaleStatusResponse(
                    "Sale status updated successfully",
                    saved.getId(),
                    saved.getSaleStatus(),
                    saved.getPaymentStatus(),
                    saved.getCourierOrderStatus(),
                    saved.getJobType()
            );
        });

        UpdateSaleStatusResponse response = saleService.updateSaleStatus(1, new UpdateSaleStatusRequest(
                "shipped",
                "paid",
                null,
                null,
                7,
                "Shipped today"
        ));

        assertThat(response.id()).isEqualTo(1);
        assertThat(response.saleStatus()).isEqualTo("shipped");
        assertThat(response.paymentStatus()).isEqualTo("paid");
        assertThat(response.jobType()).isEqualTo("shipping");
        assertThat(response.message()).isEqualTo("Sale status updated successfully");

        ArgumentCaptor<SaleStatusHistory> historyCaptor = ArgumentCaptor.forClass(SaleStatusHistory.class);
        verify(saleStatusHistoryRepository).save(historyCaptor.capture());
        assertThat(historyCaptor.getValue().getOldStatus()).isEqualTo("pending");
        assertThat(historyCaptor.getValue().getNewStatus()).isEqualTo("shipped");
        assertThat(historyCaptor.getValue().getChangedBy()).isEqualTo(7);
        assertThat(historyCaptor.getValue().getNote()).isEqualTo("Shipped today");
    }

    @Test
    @DisplayName("Given missing shelving item When updating status Then throws not found")
    void shouldRejectMissingShelvingItem() {
        when(jdbcTemplate.update(any(String.class), eq("returned"), eq(55))).thenReturn(0);

        assertThatThrownBy(() -> saleService.updateShelvingStatus(55, "returned"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Given existing shelving item When updating status Then returns typed response")
    void shouldUpdateShelvingStatus() {
        when(jdbcTemplate.update(any(String.class), eq("returned"), eq(10))).thenReturn(1);
        when(saleMapper.toShelvingStatusResponse(10, "returned"))
                .thenReturn(new UpdateShelvingStatusResponse(true, 10, "returned"));

        UpdateShelvingStatusResponse response = saleService.updateShelvingStatus(10, "returned");

        assertThat(response.success()).isTrue();
        assertThat(response.id()).isEqualTo(10);
        assertThat(response.status()).isEqualTo("returned");
    }
}
