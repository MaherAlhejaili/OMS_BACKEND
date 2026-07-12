package com.avnzor.oms_backend.shipping.entity;

import com.avnzor.oms_backend.audit.entity.JsonMapConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

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

    @Column
    private String provider;

    @Column(name = "number_of_pieces")
    private Integer numberOfPieces;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "label_url", columnDefinition = "TEXT")
    private String labelUrl;

    @Column
    private String status;

    @Convert(converter = JsonMapConverter.class)
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "raw_request")
    private Map<String, Object> rawRequest;

    @Convert(converter = JsonMapConverter.class)
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "raw_response")
    private Map<String, Object> rawResponse;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
