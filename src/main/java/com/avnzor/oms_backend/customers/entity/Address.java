package com.avnzor.oms_backend.customers.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @Column
    private String line1;

    @Column
    private String line2;

    @Column
    private String city;

    @Column(name = "postal_code")
    private String postalCode;

    @Column
    private String state;

    @Column
    private String country;

    @Column
    private String phone;

    @Column
    private String latitude;

    @Column
    private String longitude;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "mobile_verified")
    private Integer mobileVerified;

    @Column(name = "is_default")
    private Boolean defaultAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company customer;
}
