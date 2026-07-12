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

    @Column(name = "customer_email")
    private String customerEmail;

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

    @Column(name = "warning_note")
    private String warningNote;

    @Column(precision = 6, scale = 2)
    private BigDecimal total;

    @Column(name = "total_net_sale", precision = 6, scale = 2)
    private BigDecimal totalNetSale;

    @Column(name = "product_discount", precision = 6, scale = 2)
    private BigDecimal productDiscount;

    @Column(name = "order_discount_id")
    private Integer orderDiscountId;

    @Column(name = "total_discount", precision = 6, scale = 2)
    private BigDecimal totalDiscount;

    @Column(name = "order_discount", precision = 6, scale = 2)
    private BigDecimal orderDiscount;

    @Column(name = "additional_discount", precision = 6, scale = 2)
    private BigDecimal additionalDiscount;

    @Column(name = "returns_total_deducted", precision = 6, scale = 2)
    private BigDecimal returnsTotalDeducted;

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

    @Column(name = "sale_status")
    private String saleStatus;

    @Column(name = "supplier_status")
    private String supplierStatus;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "payment_term")
    private Integer paymentTerm;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "total_items")
    private Integer totalItems;

    @Column
    private Integer pos;

    @Column(precision = 6, scale = 2)
    private BigDecimal paid;

    @Column(name = "return_id")
    private Integer returnId;

    @Column(precision = 6, scale = 2)
    private BigDecimal surcharge;

    @Column
    private String attachment;

    @Column(name = "return_sale_ref")
    private String returnSaleRef;

    @Column(name = "sale_id")
    private Integer saleId;

    @Column(name = "return_sale_total", precision = 6, scale = 2)
    private BigDecimal returnSaleTotal;

    @Column(precision = 6, scale = 2)
    private BigDecimal rounding;

    @Column(name = "suspend_note")
    private String suspendNote;

    @Column
    private Boolean api;

    @Column
    private Integer shop;

    @Column(name = "address_id")
    private Integer addressId;

    @Column(name = "reserve_id")
    private Integer reserveId;

    @Column
    private String hash;

    @Column(name = "manual_payment", precision = 6, scale = 2)
    private BigDecimal manualPayment;

    @Column(precision = 6, scale = 2)
    private BigDecimal cgst;

    @Column(precision = 6, scale = 2)
    private BigDecimal sgst;

    @Column(precision = 6, scale = 2)
    private BigDecimal igst;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "sale_invoice")
    private String saleInvoice;

    @Column(name = "sequence_code")
    private String sequenceCode;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column
    private String instructions;

    @Column(name = "cost_goods_sold", precision = 6, scale = 2)
    private BigDecimal costGoodsSold;

    @Column(name = "courier_id")
    private Integer courierId;

    @Column(name = "courier_status")
    private String courierStatus;

    @Column(name = "courier_order_tracking_id")
    private String courierOrderTrackingId;

    @Column(name = "courier_order_status")
    private String courierOrderStatus;

    @Column(name = "delivery_type")
    private String deliveryType;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "pickup_location_id")
    private Integer pickupLocationId;

    @Column(name = "courier_assignment_time")
    private LocalDateTime courierAssignmentTime;

    @Column(name = "courier_pickup_time")
    private LocalDateTime courierPickupTime;

    @Column(name = "courier_delivery_time")
    private LocalDateTime courierDeliveryTime;

    @Column(name = "courier_label")
    private String courierLabel;

    @Column(name = "shipping_first_name")
    private String shippingFirstName;

    @Column(name = "shipping_last_name")
    private String shippingLastName;

    @Column(name = "shipping_address1")
    private String shippingAddress1;

    @Column(name = "shipping_address2")
    private String shippingAddress2;

    @Column(name = "shipping_phone")
    private String shippingPhone;

    @Column(name = "shipping_city")
    private String shippingCity;

    @Column(name = "shipping_zip")
    private String shippingZip;

    @Column(name = "shipping_province")
    private String shippingProvince;

    @Column(name = "shipping_country")
    private String shippingCountry;

    @Column(name = "shipping_company")
    private String shippingCompany;

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

    @Column
    private String source;

    @Column(name = "job_type")
    private String jobType;

    @Column(name = "source_name")
    private String sourceName;

    @Column(name = "shopify_tags", columnDefinition = "TEXT")
    private String shopifyTags;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleItem> items = new ArrayList<>();
}
