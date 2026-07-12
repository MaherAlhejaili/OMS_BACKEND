package com.avnzor.oms_backend.purchases.entity;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sma_purchase_orders")
@Getter
@Setter
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reference_no", length = 50, unique = true)
    private String referenceNo;

    @Column
    private LocalDateTime date;

    @Column(name = "supplier_id")
    private Integer supplierId;

    @Column
    private String supplier;

    @Column(name = "warehouse_id")
    private Integer warehouseId;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "old_total_net_purchase", precision = 12, scale = 2)
    private BigDecimal oldTotalNetPurchase;

    @Column(name = "total_net_purchase", precision = 12, scale = 2)
    private BigDecimal totalNetPurchase;

    @Column(name = "total_sale", precision = 12, scale = 2)
    private BigDecimal totalSale;

    @Column(name = "product_discount", precision = 12, scale = 2)
    private BigDecimal productDiscount;

    @Column(name = "order_discount_id")
    private Integer orderDiscountId;

    @Column(name = "order_discount", precision = 12, scale = 2)
    private BigDecimal orderDiscount;

    @Column(name = "total_discount", precision = 12, scale = 2)
    private BigDecimal totalDiscount;

    @Column(name = "product_tax", precision = 12, scale = 2)
    private BigDecimal productTax;

    @Column(name = "order_tax_id")
    private Integer orderTaxId;

    @Column(name = "order_tax", precision = 12, scale = 2)
    private BigDecimal orderTax;

    @Column(name = "total_tax", precision = 12, scale = 2)
    private BigDecimal totalTax;

    @Column(precision = 12, scale = 2)
    private BigDecimal shipping;

    @Column(name = "grand_total", precision = 12, scale = 2)
    private BigDecimal grandTotal;

    @Column(precision = 12, scale = 2)
    private BigDecimal paid;

    @Column(length = 50)
    private String status;

    @Column(name = "payment_status", length = 50)
    private String paymentStatus;

    @Column(name = "shelf_status", length = 50)
    private String shelfStatus;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column
    private String attachment;

    @Column(name = "payment_term", length = 50)
    private String paymentTerm;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "return_id")
    private Integer returnId;

    @Column(precision = 12, scale = 2)
    private BigDecimal surcharge;

    @Column(name = "return_purchase_ref", length = 50)
    private String returnPurchaseRef;

    @Column(name = "purchase_id")
    private Integer purchaseId;

    @Column(name = "return_purchase_total", precision = 12, scale = 2)
    private BigDecimal returnPurchaseTotal;

    @Column(precision = 12, scale = 2)
    private BigDecimal cgst;

    @Column(precision = 12, scale = 2)
    private BigDecimal sgst;

    @Column(precision = 12, scale = 2)
    private BigDecimal igst;

    @Column
    private String tempstatus;

    @Column
    private String lotnumber;

    @Column
    private Boolean validate;

    @Column(name = "purchase_invoice", length = 100)
    private String purchaseInvoice;

    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;

    @Column(name = "sequence_code", length = 100)
    private String sequenceCode;

    @Column(name = "notification_id")
    private Integer notificationId;

    @Column(name = "is_transfer")
    private Boolean transfer;

    @Column(name = "transfer_id")
    private Integer transferId;

    @Column(name = "location_to", length = 100)
    private String locationTo;

    @Column(name = "transfer_by")
    private Integer transferBy;

    @Column(name = "transfer_at")
    private LocalDateTime transferAt;

    @Column(name = "sending_notes", columnDefinition = "TEXT")
    private String sendingNotes;

    @Column(name = "grn_id")
    private Integer grnId;

    @Column(name = "grand_deal_discount", precision = 12, scale = 2)
    private BigDecimal grandDealDiscount;

    @Column
    private Boolean active;

    @Column(name = "total_items_received")
    private Integer totalItemsReceived;

    @Column(name = "grn_notes", columnDefinition = "TEXT")
    private String grnNotes;

    @Column(name = "received_by")
    private Integer receivedBy;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private List<PurchaseOrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private List<PurchaseOrderShelving> shelvings = new ArrayList<>();
}
