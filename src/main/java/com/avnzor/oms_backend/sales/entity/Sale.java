package com.avnzor.oms_backend.sales.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sma_sales")
@Getter
@Setter
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private LocalDateTime date;

    @Column(name = "reference_no")
    private String referenceNo;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column
    private String customer;

    @Column(name = "biller_id")
    private Integer billerId;

    @Column
    private String biller;

    @Column(name = "warehouse_id")
    private Integer warehouseId;

    @Column
    private String note;

    @Column(name = "staff_note")
    private String staffNote;

    @Column(precision = 6, scale = 2)
    private BigDecimal total;

    @Column(name = "product_discount", precision = 6, scale = 2)
    private BigDecimal productDiscount;

    @Column(name = "order_discount_id")
    private Integer orderDiscountId;

    @Column(name = "order_discount", precision = 6, scale = 2)
    private BigDecimal orderDiscount;

    @Column(name = "total_discount", precision = 6, scale = 2)
    private BigDecimal totalDiscount;

    @Column(name = "product_tax", precision = 6, scale = 2)
    private BigDecimal productTax;

    @Column(name = "order_tax_id")
    private Integer orderTaxId;

    @Column(name = "order_tax", precision = 6, scale = 2)
    private BigDecimal orderTax;

    @Column(name = "total_tax", precision = 6, scale = 2)
    private BigDecimal totalTax;

    @Column(precision = 6, scale = 2)
    private BigDecimal shipping;

    @Column(name = "grand_total", precision = 6, scale = 2)
    private BigDecimal grandTotal;

    @Column(name = "total_items")
    private Integer totalItems;

    @Column
    private Integer pos;

    @Column(name = "sale_status")
    private String saleStatus;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "job_type")
    private String jobType;

    @Column(name = "payment_term")
    private Integer paymentTerm;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(precision = 6, scale = 2)
    private BigDecimal paid;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column
    private Integer shop;

    @Column(name = "address_id")
    private Integer addressId;

    @Column
    private String hash;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "delivery_type")
    private String deliveryType;

    @Column(name = "sequence_code")
    private String sequenceCode;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "courier_id")
    private Integer courierId;

    @Column(name = "courier_order_status")
    private String courierOrderStatus;

    @Column(name = "courier_order_tracking_id")
    private String courierOrderTrackingId;

    @Column(name = "courier_label")
    private String courierLabel;

    @Column(name = "shipping_first_name")
    private String shippingFirstName;

    @Column(name = "shipping_last_name")
    private String shippingLastName;

    @Column(name = "shipping_phone")
    private String shippingPhone;

    @Column(name = "shipping_address1")
    private String shippingAddress1;

    @Column(name = "shipping_address2")
    private String shippingAddress2;

    @Column(name = "shipping_city")
    private String shippingCity;

    @Column(name = "shipping_zip")
    private String shippingZip;

    @Column(name = "shipping_province")
    private String shippingProvince;

    @Column(name = "shipping_latitude")
    private String shippingLatitude;

    @Column(name = "shipping_longitude")
    private String shippingLongitude;

    @Column(name = "shipping_name")
    private String shippingName;

    @Column(name = "shipping_country_code")
    private String shippingCountryCode;

    @Column(name = "shipping_province_code")
    private String shippingProvinceCode;

    @Column(name = "shipping_country")
    private String shippingCountry;

    @Column(name = "source_name")
    private String sourceName;

    @Column(name = "shopify_tags", columnDefinition = "TEXT")
    private String shopifyTags;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleItem> items = new ArrayList<>();
}
