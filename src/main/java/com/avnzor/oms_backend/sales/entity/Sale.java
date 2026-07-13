package com.avnzor.oms_backend.sales.entity;

import com.avnzor.oms_backend.common.persistence.LegacyBitFlag;
import com.avnzor.oms_backend.common.persistence.LegacySmallInt;
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
@Table(name = "sma_sales")
@Getter
@Setter
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "reference_no", length = 55)
    private String referenceNo;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "customer", length = 55)
    private String customer;

    @Column(name = "customer_email", length = 255)
    private String customerEmail;

    @Column(name = "biller_id")
    private Integer billerId;

    @Column(name = "biller", length = 55)
    private String biller;

    @Column(name = "warehouse_id")
    private Integer warehouseId;

    @Column(name = "note", columnDefinition = "MEDIUMTEXT")
    private String note;

    @Column(name = "staff_note", columnDefinition = "MEDIUMTEXT")
    private String staffNote;

    @Column(name = "warning_note", length = 1000)
    private String warningNote;

    @Column(name = "total", precision = 25, scale = 5)
    private BigDecimal total;

    @Column(name = "total_net_sale", precision = 25, scale = 5)
    private BigDecimal totalNetSale;

    @Column(name = "product_discount", precision = 25, scale = 5)
    private BigDecimal productDiscount;

    @Column(name = "order_discount_id", length = 20)
    private String orderDiscountId;

    @Column(name = "total_discount", precision = 25, scale = 5)
    private BigDecimal totalDiscount;

    @Column(name = "order_discount", precision = 25, scale = 5)
    private BigDecimal orderDiscount;

    @Column(name = "additional_discount", precision = 25, scale = 4)
    private BigDecimal additionalDiscount;

    @Column(name = "returns_total_deducted", precision = 25, scale = 4)
    private BigDecimal returnsTotalDeducted;

    @Column(name = "product_tax", precision = 25, scale = 2)
    private BigDecimal productTax;

    @Column(name = "order_tax_id")
    private Integer orderTaxId;

    @Column(name = "order_tax", precision = 25, scale = 5)
    private BigDecimal orderTax;

    @Column(name = "total_tax", precision = 25, scale = 5)
    private BigDecimal totalTax;

    @Column(name = "shipping", precision = 25, scale = 4)
    private BigDecimal shipping;

    @Column(name = "grand_total", precision = 25, scale = 5)
    private BigDecimal grandTotal;

    @Column(name = "sale_status", length = 20)
    private String saleStatus;

    @Column(name = "supplier_status", length = 20)
    private String supplierStatus;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus;

    @Column(name = "payment_term", length = 50)
    private String paymentTerm;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @LegacySmallInt
    @Column(name = "total_items")
    private Integer totalItems;

    @LegacyBitFlag
    @Column(name = "pos")
    private Boolean pos;

    @Column(name = "paid", precision = 25, scale = 5)
    private BigDecimal paid;

    @Column(name = "return_id")
    private Integer returnId;

    @Column(name = "surcharge", precision = 25, scale = 5)
    private BigDecimal surcharge;

    @Column(name = "attachment", length = 500)
    private String attachment;

    @Column(name = "return_sale_ref", length = 55)
    private String returnSaleRef;

    @Column(name = "sale_id")
    private Integer saleId;

    @Column(name = "return_sale_total", precision = 25, scale = 5)
    private BigDecimal returnSaleTotal;

    @Column(name = "rounding", precision = 10, scale = 2)
    private BigDecimal rounding;

    @Column(name = "suspend_note", length = 255)
    private String suspendNote;

    @LegacyBitFlag
    @Column(name = "api")
    private Boolean api;

    @LegacyBitFlag
    @Column(name = "shop")
    private Boolean shop;

    @Column(name = "address_id")
    private Integer addressId;

    @Column(name = "reserve_id")
    private Integer reserveId;

    @Column(name = "hash", length = 255)
    private String hash;

    @Column(name = "manual_payment", length = 55)
    private String manualPayment;

    @Column(name = "cgst", precision = 25, scale = 5)
    private BigDecimal cgst;

    @Column(name = "sgst", precision = 25, scale = 5)
    private BigDecimal sgst;

    @Column(name = "igst", precision = 25, scale = 5)
    private BigDecimal igst;

    @Column(name = "payment_method", length = 55)
    private String paymentMethod;

    @LegacyBitFlag
    @Column(name = "sale_invoice")
    private Boolean saleInvoice;

    @Column(name = "sequence_code", length = 255)
    private String sequenceCode;

    @Column(name = "invoice_number", length = 255)
    private String invoiceNumber;

    @Column(name = "customer_name", length = 100)
    private String customerName;

    @Column(name = "mobile_number", length = 50)
    private String mobileNumber;

    @Column(name = "instructions", columnDefinition = "MEDIUMTEXT")
    private String instructions;

    @Column(name = "cost_goods_sold", precision = 24, scale = 5)
    private BigDecimal costGoodsSold;

    @Column(name = "courier_id")
    private Integer courierId;

    @Column(name = "courier_status", length = 255)
    private String courierStatus;

    @Column(name = "courier_order_tracking_id", length = 255)
    private String courierOrderTrackingId;

    @Column(name = "courier_order_status", length = 255)
    private String courierOrderStatus;

    @Column(name = "delivery_type", length = 20)
    private String deliveryType;

    @Column(name = "coupon_code", length = 50)
    private String couponCode;

    @Column(name = "external_id", length = 255)
    private String externalId;

    @Column(name = "pickup_location_id")
    private Integer pickupLocationId;

    @Column(name = "courier_assignment_time")
    private LocalDateTime courierAssignmentTime;

    @Column(name = "courier_pickup_time")
    private LocalDateTime courierPickupTime;

    @Column(name = "courier_delivery_time")
    private LocalDateTime courierDeliveryTime;

    @Column(name = "courier_label", length = 1000)
    private String courierLabel;

    @Column(name = "shipping_first_name", length = 255)
    private String shippingFirstName;

    @Column(name = "shipping_last_name", length = 255)
    private String shippingLastName;

    @Column(name = "shipping_address1", length = 255)
    private String shippingAddress1;

    @Column(name = "shipping_address2", length = 255)
    private String shippingAddress2;

    @Column(name = "shipping_phone", length = 255)
    private String shippingPhone;

    @Column(name = "shipping_city", length = 255)
    private String shippingCity;

    @Column(name = "shipping_zip", length = 255)
    private String shippingZip;

    @Column(name = "shipping_province", length = 255)
    private String shippingProvince;

    @Column(name = "shipping_country", length = 255)
    private String shippingCountry;

    @Column(name = "shipping_company", length = 255)
    private String shippingCompany;

    @Column(name = "shipping_latitude", length = 255)
    private String shippingLatitude;

    @Column(name = "shipping_longitude", length = 255)
    private String shippingLongitude;

    @Column(name = "shipping_name", length = 255)
    private String shippingName;

    @Column(name = "shipping_country_code", length = 255)
    private String shippingCountryCode;

    @Column(name = "shipping_province_code", length = 255)
    private String shippingProvinceCode;

    @Column(name = "source")
    private Integer source;

    @Column(name = "job_type", length = 255)
    private String jobType;

    @Column(name = "source_name", length = 255)
    private String sourceName;

    @Column(name = "shopify_tags", columnDefinition = "TEXT")
    private String shopifyTags;
}
