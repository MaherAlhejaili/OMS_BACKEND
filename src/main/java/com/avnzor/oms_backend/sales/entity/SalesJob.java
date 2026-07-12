package com.avnzor.oms_backend.sales.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sma_sales_jobs")
@Getter
@Setter
public class SalesJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reference_no")
    private Integer referenceNo;

    @Column(name = "assigned_to")
    private Integer assignedTo;

    @Column(name = "assigned_by")
    private Integer assignedBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "return_id", length = 100)
    private String returnId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
