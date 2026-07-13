package com.avnzor.oms_backend.customers.entity;

import com.avnzor.oms_backend.common.persistence.LegacyTinyInt;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "group_name", length = 20)
    private String groupName;

    @Column(name = "customer_group_id")
    private Integer customerGroupId;

    @Column(name = "customer_group_name", length = 100)
    private String customerGroupName;

    @Column(name = "name", length = 55)
    private String name;

    @Column(name = "name_ar", length = 255)
    private String nameAr;

    @Column(name = "category", length = 255)
    private String category;

    @Column(name = "company", length = 255)
    private String company;

    @Column(name = "vat_no", length = 100)
    private String vatNo;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "city", length = 55)
    private String city;

    @Column(name = "state", length = 55)
    private String state;

    @Column(name = "postal_code", length = 8)
    private String postalCode;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "cf1", length = 100)
    private String cf1;

    @Column(name = "cf2", length = 100)
    private String cf2;

    @Column(name = "cf3", length = 100)
    private String cf3;

    @Column(name = "cf4", length = 100)
    private String cf4;

    @Column(name = "cf5", length = 100)
    private String cf5;

    @Column(name = "cf6", length = 100)
    private String cf6;

    @Column(name = "invoice_footer", columnDefinition = "MEDIUMTEXT")
    private String invoiceFooter;

    @Column(name = "payment_term")
    private Integer paymentTerm;

    @Column(name = "logo", length = 255)
    private String logo;

    @Column(name = "award_points")
    private Integer awardPoints;

    @Column(name = "deposit_amount", precision = 25, scale = 4)
    private BigDecimal depositAmount;

    @Column(name = "price_group_id")
    private Integer priceGroupId;

    @Column(name = "price_group_name", length = 50)
    private String priceGroupName;

    @Column(name = "gst_no", length = 100)
    private String gstNo;

    @Column(name = "ledger_account")
    private Long ledgerAccount;

    @Column(name = "sequence_code", length = 255)
    private String sequenceCode;

    @Column(name = "parent_code", length = 255)
    private String parentCode;

    @Column(name = "level")
    private Integer level;

    @Column(name = "fund_books_ledger")
    private Long fundBooksLedger;

    @Column(name = "credit_card_ledger")
    private Long creditCardLedger;

    @Column(name = "cogs_ledger")
    private Long cogsLedger;

    @Column(name = "inventory_ledger")
    private Long inventoryLedger;

    @Column(name = "sales_ledger")
    private Long salesLedger;

    @Column(name = "price_difference_ledger")
    private Long priceDifferenceLedger;

    @Column(name = "discount_ledger")
    private Long discountLedger;

    @Column(name = "vat_on_sales_ledger")
    private Long vatOnSalesLedger;

    @Column(name = "return_ledger")
    private Long returnLedger;

    @Column(name = "credit_limit", length = 255)
    private String creditLimit;

    @Column(name = "balance")
    private Float balance;

    @Column(name = "gln", length = 100)
    private String gln;

    @Column(name = "cr", length = 255)
    private String cr;

    @Column(name = "external_id")
    private Integer externalId;

    @Column(name = "cr_expiration", length = 50)
    private String crExpiration;

    @Column(name = "sfda_certificate", length = 50)
    private String sfdaCertificate;

    @Column(name = "short_address", length = 50)
    private String shortAddress;

    @Column(name = "building_number", length = 50)
    private String buildingNumber;

    @Column(name = "unit_number", length = 50)
    private String unitNumber;

    @Column(name = "additional_number", length = 50)
    private String additionalNumber;

    @Column(name = "contact_name", length = 100)
    private String contactName;

    @Column(name = "contact_number", length = 50)
    private String contactNumber;

    @Column(name = "promessory_note_amount", precision = 25, scale = 5)
    private BigDecimal promessoryNoteAmount;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "sales_agent", length = 100)
    private String salesAgent;

    @Column(name = "line2", length = 255)
    private String line2;

    @Column(name = "latitude", length = 255)
    private String latitude;

    @Column(name = "longitude", length = 255)
    private String longitude;

    @Column(name = "first_name", length = 255)
    private String firstName;

    @Column(name = "last_name", length = 255)
    private String lastName;

    @LegacyTinyInt
    @Column(name = "mobile_verified")
    private Integer mobileVerified;

    @Column(name = "status", length = 255)
    private String status;

    @Column(name = "profile_pic", length = 255)
    private String profilePic;

    @Column(name = "national_number", length = 255)
    private String nationalNumber;
}
