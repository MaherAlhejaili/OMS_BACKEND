package com.avnzor.oms_backend.products.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sma_products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String code;

    @Column
    private String name;

    @Column(name = "name_ar")
    private String nameAr;

    @Column(precision = 6, scale = 2)
    private BigDecimal cost;

    @Column(precision = 6, scale = 2)
    private BigDecimal price;

    @Column
    private String image;

    @Column
    private Integer quantity;

    @Column
    private Integer promotion;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "promo_price", precision = 6, scale = 2)
    private BigDecimal promoPrice;

    @Column(name = "product_details")
    private String productDetails;

    @Column(name = "product_details_ar")
    private String productDetailsAr;

    @Column(name = "details")
    private String highlights;

    @Column(name = "details_ar")
    private String highlightsAr;

    @Column
    private String type;

    @Column(name = "bundle_items_ids", columnDefinition = "TEXT")
    private String bundleItemsIds;

    @Column
    private String slug;

    @Column(name = "brand")
    private Integer brandId;

    @Column
    private Integer featured;

    @Column(name = "special_offer")
    private Integer specialOffer;

    @Column(name = "best_seller")
    private Integer bestSeller;

    @Column(name = "sequence_code")
    private String sequenceCode;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "speciality_id")
    private Integer specialityId;

    @Column(name = "topic_id")
    private Integer topicId;

    @Column(name = "tax_rate")
    private Integer taxRate;

    @Column(name = "subcategory_id")
    private Integer subCategoryId;

    @Column(columnDefinition = "TEXT")
    private String tags;
}
