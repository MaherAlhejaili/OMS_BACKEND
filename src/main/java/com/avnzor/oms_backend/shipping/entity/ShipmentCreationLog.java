package com.avnzor.oms_backend.shipping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "sma_shipment_creations")
@Getter
@Setter
public class ShipmentCreationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "provider", length = 255)
    private String provider;

    @Column(name = "number_of_pieces")
    private Integer numberOfPieces;

    @Column(name = "tracking_number", length = 255)
    private String trackingNumber;

    @Column(name = "label_url", columnDefinition = "TEXT")
    private String labelUrl;

    @Column(name = "status", length = 255)
    private String status;

    @Column(name = "raw_request")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String rawRequest;

    @Column(name = "raw_response")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String rawResponse;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
