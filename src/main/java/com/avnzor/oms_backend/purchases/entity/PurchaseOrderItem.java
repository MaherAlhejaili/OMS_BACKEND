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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sma_purchase_order_items")
@Getter
@Setter
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    private PurchaseOrder purchaseOrder;

    @Column(name = "transfer_id")
    private Integer transferId;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_code", length = 100)
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "option_id")
    private Integer optionId;

    @Column(name = "net_unit_cost", precision = 12, scale = 2)
    private BigDecimal netUnitCost;

    @Column
    private Integer quantity;

    @Column(name = "warehouse_id")
    private Integer warehouseId;

    @Column(name = "item_tax", precision = 12, scale = 2)
    private BigDecimal itemTax;

    @Column(name = "tax_rate_id")
    private Integer taxRateId;

    @Column(precision = 12, scale = 2)
    private BigDecimal tax;

    @Column(precision = 12, scale = 2)
    private BigDecimal discount;

    @Column(name = "item_discount", precision = 12, scale = 2)
    private BigDecimal itemDiscount;

    @Column
    private LocalDate expiry;

    @Column(precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "quantity_balance")
    private Integer quantityBalance;

    @Column
    private LocalDateTime date;

    @Column(length = 50)
    private String status;

    @Column(name = "unit_cost", precision = 12, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "real_unit_cost", precision = 12, scale = 2)
    private BigDecimal realUnitCost;

    @Column(name = "sale_price", precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "quantity_received")
    private Integer quantityReceived;

    @Column(name = "supplier_part_no", length = 100)
    private String supplierPartNo;

    @Column(name = "purchase_item_id")
    private Integer purchaseItemId;

    @Column(name = "product_unit_id")
    private Integer productUnitId;

    @Column(name = "product_unit_code", length = 50)
    private String productUnitCode;

    @Column(name = "unit_quantity", precision = 10, scale = 2)
    private BigDecimal unitQuantity;

    @Column(precision = 12, scale = 2)
    private BigDecimal gst;

    @Column(precision = 12, scale = 2)
    private BigDecimal cgst;

    @Column(precision = 12, scale = 2)
    private BigDecimal sgst;

    @Column(precision = 12, scale = 2)
    private BigDecimal igst;

    @Column(name = "base_unit_cost", precision = 12, scale = 2)
    private BigDecimal baseUnitCost;

    @Column(precision = 12, scale = 2)
    private BigDecimal subtotal2;

    @Column
    private String batchno;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @Column
    private Integer bonus;

    @Column(precision = 12, scale = 2)
    private BigDecimal discount1;

    @Column(precision = 12, scale = 2)
    private BigDecimal discount2;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalbeforevat;

    @Column(name = "main_net", precision = 12, scale = 2)
    private BigDecimal mainNet;

    @Column(name = "warehouse_shelf", length = 100)
    private String warehouseShelf;

    @Column(name = "avz_item_code", length = 50)
    private String avzItemCode;

    @Column(name = "second_discount_value", precision = 12, scale = 2)
    private BigDecimal secondDiscountValue;

    @Column(name = "returned_quantity")
    private Integer returnedQuantity;

    @Column(name = "is_transfer")
    private Boolean transfer;

    @Column(precision = 12, scale = 2)
    private BigDecimal discount3;

    @Column(name = "third_discount_value", precision = 12, scale = 2)
    private BigDecimal thirdDiscountValue;

    @Column(name = "deal_discount", precision = 12, scale = 2)
    private BigDecimal dealDiscount;

    @Column(name = "deal_discount_value", precision = 12, scale = 2)
    private BigDecimal dealDiscountValue;

    @Column(name = "actual_quantity")
    private Integer actualQuantity;

    @Column(name = "scanned_quantity")
    private Integer scannedQuantity;

    @Column(name = "new_scanned_quantity")
    private Integer newScannedQuantity;

    @Column(name = "grn_comments", columnDefinition = "TEXT")
    private String grnComments;
}
