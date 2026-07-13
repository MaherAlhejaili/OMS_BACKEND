package com.avnzor.oms_backend.warehouse.entity;

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
@Table(name = "sma_warehouse_locations")
@Getter
@Setter
public class WarehouseLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "zone", length = 50)
    private String zone;

    @Column(name = "rack", length = 50)
    private String rack;

    @Column(name = "section", length = 50)
    private String section;

    @Column(name = "shelf", length = 50)
    private String shelf;

    @Column(name = "box", length = 50)
    private String box;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
