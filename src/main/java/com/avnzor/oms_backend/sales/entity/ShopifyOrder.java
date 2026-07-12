package com.avnzor.oms_backend.sales.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sma_shopify_orders")
@Getter
@Setter
public class ShopifyOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "shopify_order_id")
    private Long shopifyOrderId;

    @Column(name = "order_number", length = 50)
    private String orderNumber;

    @Column(columnDefinition = "LONGTEXT")
    private String payload;

    @Column(name = "wms_status", length = 20)
    private String wmsStatus;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
