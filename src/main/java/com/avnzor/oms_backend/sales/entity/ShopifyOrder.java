package com.avnzor.oms_backend.sales.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "sma_shopify_orders")
@Getter
@Setter
public class ShopifyOrder {

    @Column(name = "id", unique = true, nullable = false, insertable = false, updatable = false)
    private Integer id;

    @Id
    @Column(name = "shopify_order_id", nullable = false)
    private Long shopifyOrderId;

    @Column(name = "order_number", nullable = false, length = 50)
    private String orderNumber;

    @Column(name = "payload", nullable = false)
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String payload;

    @Column(name = "wms_status", length = 20)
    private String wmsStatus;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
