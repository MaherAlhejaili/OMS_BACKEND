package com.avnzor.oms_backend.sales.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "sma_sale_status_history",
        indexes = @Index(name = "idx_sale_status_history_sale_id", columnList = "sale_id")
)
@Getter
@Setter
public class SaleStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sale_id", nullable = false)
    private Integer saleId;

    @Column(name = "old_status", length = 100)
    private String oldStatus;

    @Column(name = "new_status", nullable = false, length = 100)
    private String newStatus;

    @Column(name = "changed_by")
    private Integer changedBy;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
}
