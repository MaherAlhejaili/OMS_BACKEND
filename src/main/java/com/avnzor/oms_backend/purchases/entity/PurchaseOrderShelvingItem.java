package com.avnzor.oms_backend.purchases.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sma_purchase_order_shelving_items")
@Getter
@Setter
public class PurchaseOrderShelvingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelving_id")
    private PurchaseOrderShelving shelving;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "batch_no")
    private String batchNo;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column
    private Float qty;

    @Column
    private String status;

    @Column(name = "time_stamp")
    private LocalDateTime timeStamp;

    @Column(name = "cleaned_product_code", insertable = false, updatable = false)
    private String cleanedProductCode;
}
