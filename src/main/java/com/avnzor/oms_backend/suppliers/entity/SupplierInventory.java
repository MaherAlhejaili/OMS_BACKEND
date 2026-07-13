package com.avnzor.oms_backend.suppliers.entity;

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
@Table(name = "sma_supplier_inventory")
@Getter
@Setter
public class SupplierInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "supplier_id")
    private Integer supplierId;

    @Column(name = "supplier_inventory_id")
    private Integer supplierInventoryId;

    @Column(name = "source_id")
    private Integer sourceId;

    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "product_code", length = 255)
    private String productCode;

    @Column(name = "quantity", precision = 25, scale = 2)
    private BigDecimal quantity;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;
}
