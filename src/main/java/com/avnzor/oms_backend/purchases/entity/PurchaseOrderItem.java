package com.avnzor.oms_backend.purchases.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sma_purchase_order_items")
@Getter
@Setter
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "purchase_id")
    private Integer purchaseId;

    @Column(name = "transfer_id")
    private Integer transferId;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_code", length = 50)
    private String productCode;

    @Column(name = "product_name", length = 255)
    private String productName;

    @Column(name = "option_id")
    private Integer optionId;

    @Column(name = "net_unit_cost", precision = 25, scale = 5)
    private BigDecimal netUnitCost;

    @Column(name = "quantity", precision = 15, scale = 5)
    private BigDecimal quantity;

    @Column(name = "warehouse_id")
    private Integer warehouseId;

    @Column(name = "item_tax", precision = 25, scale = 5)
    private BigDecimal itemTax;

    @Column(name = "tax_rate_id")
    private Integer taxRateId;

    @Column(name = "tax", length = 20)
    private String tax;

    @Column(name = "discount", length = 20)
    private String discount;

    @Column(name = "item_discount", precision = 25, scale = 5)
    private BigDecimal itemDiscount;

    @Column(name = "expiry")
    private LocalDate expiry;

    @Column(name = "subtotal", precision = 25, scale = 5)
    private BigDecimal subtotal;

    @Column(name = "quantity_balance", precision = 15, scale = 5)
    private BigDecimal quantityBalance;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "unit_cost", precision = 25, scale = 5)
    private BigDecimal unitCost;

    @Column(name = "real_unit_cost", precision = 25, scale = 5)
    private BigDecimal realUnitCost;

    @Column(name = "sale_price", precision = 25, scale = 5)
    private BigDecimal salePrice;

    @Column(name = "quantity_received", precision = 15, scale = 5)
    private BigDecimal quantityReceived;

    @Column(name = "supplier_part_no", length = 50)
    private String supplierPartNo;

    @Column(name = "purchase_item_id")
    private Integer purchaseItemId;

    @Column(name = "product_unit_id")
    private Integer productUnitId;

    @Column(name = "product_unit_code", length = 10)
    private String productUnitCode;

    @Column(name = "unit_quantity", precision = 15, scale = 5)
    private BigDecimal unitQuantity;

    @Column(name = "gst", length = 20)
    private String gst;

    @Column(name = "cgst", precision = 25, scale = 4)
    private BigDecimal cgst;

    @Column(name = "sgst", precision = 25, scale = 4)
    private BigDecimal sgst;

    @Column(name = "igst", precision = 25, scale = 4)
    private BigDecimal igst;

    @Column(name = "base_unit_cost", precision = 25, scale = 4)
    private BigDecimal baseUnitCost;

    @Column(name = "subtotal2", precision = 25, scale = 4)
    private BigDecimal subtotal2;

    @Column(name = "batchno", length = 50)
    private String batchno;

    @Column(name = "serial_number", length = 200)
    private String serialNumber;

    @Column(name = "bonus", precision = 20, scale = 2)
    private BigDecimal bonus;

    @Column(name = "discount1", precision = 25, scale = 5)
    private BigDecimal discount1;

    @Column(name = "discount2", precision = 24, scale = 2)
    private BigDecimal discount2;

    @Column(name = "totalbeforevat", precision = 25, scale = 5)
    private BigDecimal totalbeforevat;

    @Column(name = "main_net", precision = 25, scale = 5)
    private BigDecimal mainNet;

    @Column(name = "warehouse_shelf", length = 50)
    private String warehouseShelf;

    @Column(name = "avz_item_code", length = 50)
    private String avzItemCode;

    @Column(name = "second_discount_value", precision = 25, scale = 5)
    private BigDecimal secondDiscountValue;

    @Column(name = "returned_quantity", precision = 15, scale = 5)
    private BigDecimal returnedQuantity;

    @Column(name = "is_transfer")
    private Integer transfer;

    @Column(name = "discount3", precision = 25, scale = 5)
    private BigDecimal discount3;

    @Column(name = "third_discount_value", precision = 25, scale = 5)
    private BigDecimal thirdDiscountValue;

    @Column(name = "deal_discount", precision = 25, scale = 5)
    private BigDecimal dealDiscount;

    @Column(name = "deal_discount_value", precision = 25, scale = 5)
    private BigDecimal dealDiscountValue;

    @Column(name = "actual_quantity", precision = 25, scale = 5)
    private BigDecimal actualQuantity;

    @Column(name = "grn_comments", precision = 25, scale = 5)
    private BigDecimal grnComments;

    @Column(name = "scanned_quantity", precision = 25, scale = 5)
    private BigDecimal scannedQuantity;

    @Column(name = "original_quantity", precision = 25, scale = 5)
    private BigDecimal originalQuantity;

    @Column(name = "old_scanned_quantity", precision = 25, scale = 5)
    private BigDecimal oldScannedQuantity;

    @Column(name = "scanned_time")
    private LocalDateTime scannedTime;

    @Column(name = "new_scanned_quantity")
    private Integer newScannedQuantity;

    @Column(name = "shelf_life", length = 255)
    private String shelfLife;

    @Column(name = "net_unit_cost_old", length = 255)
    private String netUnitCostOld;
}
