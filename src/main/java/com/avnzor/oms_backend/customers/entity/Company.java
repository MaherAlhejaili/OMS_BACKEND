package com.avnzor.oms_backend.customers.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sma_companies")
@Getter
@Setter
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private String company;

    @Column
    private String email;

    @Column
    private String phone;

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "group_name")
    private String groupName;

    @Column
    private String address;

    @Column
    private String city;

    @Column
    private String state;

    @Column
    private String country;

    @Column(name = "sequence_code")
    private String sequenceCode;

    @Column
    private String line2;

    @Column
    private String latitude;

    @Column
    private String longitude;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "mobile_verified")
    private Integer mobileVerified;

    @Column(name = "profile_pic")
    private String profilePic;
}
