package com.avnzor.oms_backend.sales.entity;

import com.avnzor.oms_backend.common.persistence.LegacyBitFlag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sma_sale_items")
@Getter
@Setter
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sale_id")
    private Integer saleId;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_code", length = 55)
    private String productCode;

    @Column(name = "product_name", length = 255)
    private String productName;

    @Column(name = "product_type", length = 55)
    private String productType;

    @Column(name = "option_id")
    private Integer optionId;

    @Column(name = "net_cost", precision = 6, scale = 2)
    private BigDecimal netCost;

    @Column(name = "real_cost", precision = 25, scale = 5)
    private BigDecimal realCost;

    @Column(name = "net_unit_price", precision = 25, scale = 5)
    private BigDecimal netUnitPrice;

    @Column(name = "unit_price", precision = 25, scale = 5)
    private BigDecimal unitPrice;

    @Column(name = "quantity", precision = 15, scale = 4)
    private BigDecimal quantity;

    @Column(name = "warehouse_id")
    private Integer warehouseId;

    @Column(name = "item_tax", precision = 25, scale = 5)
    private BigDecimal itemTax;

    @Column(name = "tax_rate_id")
    private Integer taxRateId;

    @Column(name = "tax", precision = 25, scale = 5)
    private BigDecimal tax;

    @Column(name = "discount", length = 55)
    private String discount;

    @Column(name = "item_discount", precision = 25, scale = 16)
    private BigDecimal itemDiscount;

    @Column(name = "subtotal", precision = 25, scale = 5)
    private BigDecimal subtotal;

    @Column(name = "serial_no", length = 55)
    private String serialNo;

    @Column(name = "expiry")
    private LocalDate expiry;

    @Column(name = "batch_no", length = 55)
    private String batchNo;

    @Column(name = "serial_number", length = 200)
    private String serialNumber;

    @Column(name = "lot_no", length = 55)
    private String lotNo;

    @Column(name = "real_unit_price", precision = 25, scale = 5)
    private BigDecimal realUnitPrice;

    @Column(name = "sale_item_id")
    private Integer saleItemId;

    @Column(name = "product_unit_id")
    private Integer productUnitId;

    @Column(name = "product_unit_code", length = 10)
    private String productUnitCode;

    @Column(name = "unit_quantity", precision = 15, scale = 4)
    private BigDecimal unitQuantity;

    @Column(name = "comment", columnDefinition = "MEDIUMTEXT")
    private String comment;

    @Column(name = "gst", length = 55)
    private String gst;

    @Column(name = "cgst", precision = 25, scale = 2)
    private BigDecimal cgst;

    @Column(name = "sgst", precision = 25, scale = 2)
    private BigDecimal sgst;

    @Column(name = "igst", precision = 25, scale = 2)
    private BigDecimal igst;

    @Column(name = "subtotal2", precision = 24, scale = 5)
    private BigDecimal subtotal2;

    @Column(name = "bonus", precision = 24, scale = 2)
    private BigDecimal bonus;

    @Column(name = "discount1", precision = 24, scale = 5)
    private BigDecimal discount1;

    @Column(name = "discount2", precision = 24, scale = 5)
    private BigDecimal discount2;

    @Column(name = "totalbeforevat", precision = 24, scale = 5)
    private BigDecimal totalbeforevat;

    @Column(name = "main_net", precision = 24, scale = 5)
    private BigDecimal mainNet;

    @Column(name = "avz_item_code", length = 50)
    private String avzItemCode;

    @Column(name = "second_discount_value", precision = 25, scale = 5)
    private BigDecimal secondDiscountValue;

    @Column(name = "discount3", precision = 25, scale = 5)
    private BigDecimal discount3;

    @Column(name = "third_discount_value", precision = 25, scale = 5)
    private BigDecimal thirdDiscountValue;

    @Column(name = "ordered_quantity", precision = 25, scale = 5)
    private BigDecimal orderedQuantity;

    @LegacyBitFlag
    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "is_reserved")
    private Integer reserved;
}
