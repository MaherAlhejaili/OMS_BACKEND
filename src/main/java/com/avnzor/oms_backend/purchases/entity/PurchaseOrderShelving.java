package com.avnzor.oms_backend.purchases.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sma_purchase_order_shelving")
@Getter
@Setter
public class PurchaseOrderShelving {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id")
    private PurchaseOrder purchaseOrder;

    @Column(name = "sale_id")
    private Integer saleId;

    @Column(name = "zone_number")
    private Integer zoneNumber;

    @Column(name = "rack_number")
    private String rackNumber;

    @Column(name = "rack_unlock_time")
    private LocalDateTime rackUnlockTime;

    @Column(name = "rack_lock_time")
    private LocalDateTime rackLockTime;

    @Column(name = "box_number")
    private String boxNumber;

    @Column(name = "box_unlock_time")
    private LocalDateTime boxUnlockTime;

    @Column(name = "box_lock_time")
    private LocalDateTime boxLockTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(length = 50)
    private String status;

    @Column(name = "rack_number_old")
    private String rackNumberOld;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "shelving", cascade = CascadeType.ALL)
    private List<PurchaseOrderShelvingItem> shelvingItems = new ArrayList<>();
}
