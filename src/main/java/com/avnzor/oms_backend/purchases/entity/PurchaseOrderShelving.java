package com.avnzor.oms_backend.purchases.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sma_purchase_order_shelving")
@Getter
@Setter
public class PurchaseOrderShelving {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "po_id")
    private Integer poId;

    @Column(name = "sale_id")
    private Integer saleId;

    @Column(name = "zone_number", length = 50)
    private String zoneNumber;

    @Column(name = "rack_number", length = 50)
    private String rackNumber;

    @Column(name = "rack_unlock_time")
    private LocalDateTime rackUnlockTime;

    @Column(name = "rack_lock_time")
    private LocalDateTime rackLockTime;

    @Column(name = "box_number", length = 50)
    private String boxNumber;

    @Column(name = "box_unlock_time")
    private LocalDateTime boxUnlockTime;

    @Column(name = "box_lock_time")
    private LocalDateTime boxLockTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "status", length = 255)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "rack_number_old", length = 50)
    private String rackNumberOld;
}
