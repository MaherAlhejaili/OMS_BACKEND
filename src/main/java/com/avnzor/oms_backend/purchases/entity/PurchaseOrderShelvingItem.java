package com.avnzor.oms_backend.purchases.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sma_purchase_order_shelving_items")
@Getter
@Setter
public class PurchaseOrderShelvingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "shelving_id")
    private Integer shelvingId;

    @Column(name = "product_code", length = 255)
    private String productCode;

    @Column(name = "batch_no", length = 255)
    private String batchNo;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "qty")
    private Float qty;

    @Column(name = "status", length = 255)
    private String status;

    @Column(name = "time_stamp")
    private LocalDateTime timeStamp;

    @Column(name = "cleaned_product_code", length = 100)
    private String cleanedProductCode;
}
