package com.avnzor.oms_backend.warehouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(length = 50)
    private String zone;

    @Column(length = 50)
    private String rack;

    @Column(length = 50)
    private String section;

    @Column(length = 50)
    private String shelf;

    @Column(length = 50)
    private String box;

    @Column(length = 50)
    private String status;
}
