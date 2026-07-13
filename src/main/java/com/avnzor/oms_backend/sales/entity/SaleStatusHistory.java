package com.avnzor.oms_backend.sales.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sma_sale_status_history")
@Getter
@Setter
public class SaleStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sale_id")
    private Integer saleId;

    @Column(name = "old_status", length = 100)
    private String oldStatus;

    @Column(name = "new_status", length = 100)
    private String newStatus;

    @Column(name = "changed_by")
    private Integer changedBy;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;
}
