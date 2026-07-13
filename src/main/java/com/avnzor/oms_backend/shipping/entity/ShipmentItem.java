package com.avnzor.oms_backend.shipping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sma_shipment_items")
@Getter
@Setter
public class ShipmentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "product_code", nullable = false, length = 255)
    private String productCode;

    @Column(name = "qty", nullable = false, precision = 15, scale = 4)
    private BigDecimal qty;

    @Column(name = "tracking_id", nullable = false, length = 255)
    private String trackingId;

    @Column(name = "carrier", nullable = false, length = 255)
    private String carrier;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
