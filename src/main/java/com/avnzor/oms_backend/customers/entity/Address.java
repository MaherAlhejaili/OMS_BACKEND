package com.avnzor.oms_backend.customers.entity;

import com.avnzor.oms_backend.common.persistence.LegacyTinyInt;
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
@Table(name = "sma_addresses")
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "company_id")
    private Integer companyId;

    @Column(name = "line1", length = 50)
    private String line1;

    @Column(name = "line2", length = 50)
    private String line2;

    @Column(name = "city", length = 25)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "state", length = 25)
    private String state;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "latitude", length = 50)
    private String latitude;

    @Column(name = "longitude", length = 50)
    private String longitude;

    @LegacyTinyInt
    @Column(name = "is_default")
    private Integer defaultAddress;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @LegacyTinyInt
    @Column(name = "mobile_verified")
    private Integer mobileVerified;
}
