package com.avnzor.oms_backend.sales.entity;

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

@Entity
@Table(name = "sma_sale_items")
@Getter
@Setter
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_type")
    private String productType;

    @Column(name = "option_id")
    private Integer optionId;

    @Column(name = "net_cost", precision = 6, scale = 2)
    private BigDecimal netCost;

    @Column(name = "real_cost", precision = 6, scale = 2)
    private BigDecimal realCost;

    @Column(name = "net_unit_price", precision = 6, scale = 2)
    private BigDecimal netUnitPrice;

    @Column(name = "unit_price", precision = 6, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 6, scale = 2)
    private BigDecimal quantity;

    @Column(name = "warehouse_id")
    private Integer warehouseId;

    @Column(name = "item_tax", precision = 25, scale = 4)
    private BigDecimal itemTax;

    @Column(name = "tax_rate_id")
    private Integer taxRateId;

    @Column(length = 55)
    private String tax;

    @Column(length = 55)
    private String discount;

    @Column(name = "item_discount", precision = 25, scale = 4)
    private BigDecimal itemDiscount;

    @Column(precision = 25, scale = 4)
    private BigDecimal subtotal;

    @Column(name = "serial_no")
    private String serialNo;

    @Column
    private LocalDate expiry;

    @Column(name = "batch_no")
    private String batchNo;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "lot_no")
    private String lotNo;

    @Column(name = "real_unit_price", precision = 25, scale = 4)
    private BigDecimal realUnitPrice;

    @Column(name = "sale_item_id")
    private Integer saleItemId;

    @Column(name = "product_unit_id")
    private Integer productUnitId;

    @Column(name = "product_unit_code", length = 10)
    private String productUnitCode;

    @Column(name = "unit_quantity", precision = 15, scale = 4)
    private BigDecimal unitQuantity;

    @Column
    private String comment;

    @Column(precision = 25, scale = 4)
    private BigDecimal gst;

    @Column(precision = 25, scale = 4)
    private BigDecimal cgst;

    @Column(precision = 25, scale = 4)
    private BigDecimal sgst;

    @Column(precision = 25, scale = 4)
    private BigDecimal igst;

    @Column(precision = 25, scale = 4)
    private BigDecimal subtotal2;

    @Column
    private Boolean bonus;

    @Column(length = 55)
    private String discount1;

    @Column(length = 55)
    private String discount2;

    @Column(precision = 24, scale = 4)
    private BigDecimal totalbeforevat;

    @Column(name = "main_net", precision = 24, scale = 4)
    private BigDecimal mainNet;

    @Column(name = "avz_item_code", length = 50)
    private String avzItemCode;

    @Column(name = "second_discount_value", precision = 25, scale = 4)
    private BigDecimal secondDiscountValue;

    @Column(length = 55)
    private String discount3;

    @Column(name = "third_discount_value", precision = 25, scale = 4)
    private BigDecimal thirdDiscountValue;

    @Column(name = "ordered_quantity", precision = 6, scale = 2)
    private BigDecimal orderedQuantity;

    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "is_reserved")
    private Boolean reserved;

    @Column(name = "is_external")
    private Boolean external;
}
