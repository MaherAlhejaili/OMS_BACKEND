package com.avnzor.oms_backend.purchases.entity;

import com.avnzor.oms_backend.common.persistence.LegacyBitFlag;
import com.avnzor.oms_backend.common.persistence.LegacyTinyInt;
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
@Table(name = "sma_purchase_orders")
@Getter
@Setter
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reference_no", length = 55)
    private String referenceNo;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "supplier_id")
    private Integer supplierId;

    @Column(name = "supplier", length = 55)
    private String supplier;

    @Column(name = "warehouse_id")
    private Integer warehouseId;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "total", precision = 25, scale = 5)
    private BigDecimal total;

    @Column(name = "old_total_net_purchase", precision = 25, scale = 2)
    private BigDecimal oldTotalNetPurchase;

    @Column(name = "total_net_purchase", precision = 25, scale = 5)
    private BigDecimal totalNetPurchase;

    @Column(name = "total_sale", precision = 25, scale = 5)
    private BigDecimal totalSale;

    @Column(name = "product_discount", precision = 25, scale = 2)
    private BigDecimal productDiscount;

    @Column(name = "order_discount_id", length = 20)
    private String orderDiscountId;

    @Column(name = "order_discount", precision = 25, scale = 5)
    private BigDecimal orderDiscount;

    @Column(name = "total_discount", precision = 25, scale = 5)
    private BigDecimal totalDiscount;

    @Column(name = "product_tax", precision = 25, scale = 5)
    private BigDecimal productTax;

    @Column(name = "order_tax_id")
    private Integer orderTaxId;

    @Column(name = "order_tax", precision = 25, scale = 5)
    private BigDecimal orderTax;

    @Column(name = "total_tax", precision = 25, scale = 5)
    private BigDecimal totalTax;

    @Column(name = "shipping", precision = 25, scale = 2)
    private BigDecimal shipping;

    @Column(name = "grand_total", precision = 25, scale = 5)
    private BigDecimal grandTotal;

    @Column(name = "paid", precision = 25, scale = 5)
    private BigDecimal paid;

    @Column(name = "status", length = 55)
    private String status;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus;

    @Column(name = "shelf_status", length = 50)
    private String shelfStatus;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "attachment", length = 55)
    private String attachment;

    @LegacyTinyInt
    @Column(name = "payment_term")
    private Integer paymentTerm;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "return_id")
    private Integer returnId;

    @Column(name = "surcharge", precision = 25, scale = 2)
    private BigDecimal surcharge;

    @Column(name = "return_purchase_ref", length = 55)
    private String returnPurchaseRef;

    @Column(name = "purchase_id")
    private Integer purchaseId;

    @Column(name = "return_purchase_total", precision = 25, scale = 2)
    private BigDecimal returnPurchaseTotal;

    @Column(name = "cgst", precision = 25, scale = 2)
    private BigDecimal cgst;

    @Column(name = "sgst", precision = 25, scale = 2)
    private BigDecimal sgst;

    @Column(name = "igst", precision = 25, scale = 2)
    private BigDecimal igst;

    @Column(name = "tempstatus", length = 150)
    private String tempstatus;

    @Column(name = "lotnumber", length = 150)
    private String lotnumber;

    @Column(name = "validate", length = 50)
    private String validate;

    @LegacyBitFlag
    @Column(name = "purchase_invoice")
    private Boolean purchaseInvoice;

    @Column(name = "invoice_number", length = 255)
    private String invoiceNumber;

    @Column(name = "sequence_code", length = 255)
    private String sequenceCode;

    @Column(name = "notification_id", length = 255)
    private String notificationId;

    @Column(name = "is_transfer")
    private Integer transfer;

    @Column(name = "transfer_id")
    private Integer transferId;

    @Column(name = "location_to")
    private Integer locationTo;

    @Column(name = "transfer_by")
    private Integer transferBy;

    @Column(name = "transfer_at")
    private LocalDateTime transferAt;

    @Column(name = "sending_notes", columnDefinition = "TEXT")
    private String sendingNotes;

    @Column(name = "grn_id")
    private Integer grnId;

    @Column(name = "grand_deal_discount", precision = 25, scale = 5)
    private BigDecimal grandDealDiscount;

    @LegacyBitFlag
    @Column(name = "active")
    private Boolean active;

    @Column(name = "total_items_received", precision = 25, scale = 5)
    private BigDecimal totalItemsReceived;

    @Column(name = "grn_notes", columnDefinition = "TEXT")
    private String grnNotes;

    @Column(name = "received_by")
    private Integer receivedBy;

    @Column(name = "received_date")
    private LocalDate receivedDate;

    @Column(name = "receiving_document", length = 255)
    private String receivingDocument;

    @Column(name = "department_id")
    private Integer departmentId;

    @LegacyBitFlag
    @Column(name = "marked_shelved")
    private Boolean markedShelved;

    @LegacyBitFlag
    @Column(name = "flagged_supplier")
    private Boolean flaggedSupplier;
}
