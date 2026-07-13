package com.avnzor.oms_backend.products.entity;

import com.avnzor.oms_backend.common.persistence.LegacyBitFlag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sma_products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "name_ar", length = 255)
    private String nameAr;

    @Column(name = "unit")
    private Integer unit;

    @Column(name = "cost", precision = 25, scale = 4)
    private BigDecimal cost;

    @Column(name = "price", precision = 25, scale = 4)
    private BigDecimal price;

    @Column(name = "alert_quantity", precision = 15, scale = 4)
    private BigDecimal alertQuantity;

    @Column(name = "image", length = 2000)
    private String image;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "subcategory_id")
    private Integer subcategoryId;

    @Column(name = "cf1", length = 255)
    private String cf1;

    @Column(name = "cf2", length = 255)
    private String cf2;

    @Column(name = "cf3", length = 255)
    private String cf3;

    @Column(name = "cf4", length = 255)
    private String cf4;

    @Column(name = "cf5", length = 255)
    private String cf5;

    @Column(name = "cf6", length = 255)
    private String cf6;

    @Column(name = "quantity", precision = 15, scale = 4)
    private BigDecimal quantity;

    @Column(name = "tax_rate")
    private Integer taxRate;

    @LegacyBitFlag
    @Column(name = "track_quantity")
    private Boolean trackQuantity;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "warehouse")
    private Integer warehouse;

    @Column(name = "barcode_symbology", length = 55)
    private String barcodeSymbology;

    @Column(name = "file", length = 100)
    private String file;

    @Column(name = "product_details", columnDefinition = "TEXT")
    private String productDetails;

    @LegacyBitFlag
    @Column(name = "tax_method")
    private Boolean taxMethod;

    @Column(name = "type", length = 55)
    private String type;

    @Column(name = "type2", length = 55)
    private String type2;

    @Column(name = "supplier1")
    private Integer supplier1;

    @Column(name = "supplier1price", precision = 25, scale = 4)
    private BigDecimal supplier1price;

    @Column(name = "supplier2")
    private Integer supplier2;

    @Column(name = "supplier2price", precision = 25, scale = 4)
    private BigDecimal supplier2price;

    @Column(name = "supplier3")
    private Integer supplier3;

    @Column(name = "supplier3price", precision = 25, scale = 4)
    private BigDecimal supplier3price;

    @Column(name = "supplier4")
    private Integer supplier4;

    @Column(name = "supplier4price", precision = 25, scale = 4)
    private BigDecimal supplier4price;

    @Column(name = "supplier5")
    private Integer supplier5;

    @Column(name = "supplier5price", precision = 25, scale = 4)
    private BigDecimal supplier5price;

    @LegacyBitFlag
    @Column(name = "promotion")
    private Boolean promotion;

    @Column(name = "promo_price", precision = 25, scale = 4)
    private BigDecimal promoPrice;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "supplier1_part_no", length = 50)
    private String supplier1PartNo;

    @Column(name = "supplier2_part_no", length = 50)
    private String supplier2PartNo;

    @Column(name = "supplier3_part_no", length = 50)
    private String supplier3PartNo;

    @Column(name = "supplier4_part_no", length = 50)
    private String supplier4PartNo;

    @Column(name = "supplier5_part_no", length = 50)
    private String supplier5PartNo;

    @Column(name = "sale_unit")
    private Integer saleUnit;

    @Column(name = "purchase_unit")
    private Integer purchaseUnit;

    @Column(name = "brand")
    private Integer brand;

    @Column(name = "slug", length = 1000)
    private String slug;

    @LegacyBitFlag
    @Column(name = "featured")
    private Boolean featured;

    @LegacyBitFlag
    @Column(name = "special_offer")
    private Boolean specialOffer;

    @Column(name = "weight", precision = 10, scale = 4)
    private BigDecimal weight;

    @Column(name = "hsn_code")
    private Integer hsnCode;

    @Column(name = "views")
    private Integer views;

    @LegacyBitFlag
    @Column(name = "hide")
    private Boolean hide;

    @Column(name = "second_name", length = 255)
    private String secondName;

    @LegacyBitFlag
    @Column(name = "hide_pos")
    private Boolean hidePos;

    @Column(name = "trade_name", length = 250)
    private String tradeName;

    @Column(name = "manufacture_name", length = 250)
    private String manufactureName;

    @Column(name = "main_agent", length = 250)
    private String mainAgent;

    @Column(name = "purchase_account")
    private Long purchaseAccount;

    @Column(name = "sale_account")
    private Long saleAccount;

    @Column(name = "inventory_account")
    private Long inventoryAccount;

    @Column(name = "incentive_value", length = 50)
    private String incentiveValue;

    @Column(name = "incentive_qty", length = 50)
    private String incentiveQty;

    @Column(name = "sequence_code", length = 255)
    private String sequenceCode;

    @LegacyBitFlag
    @Column(name = "draft")
    private Boolean draft;

    @LegacyBitFlag
    @Column(name = "google_merch")
    private Boolean googleMerch;

    @LegacyBitFlag
    @Column(name = "imported")
    private Boolean imported;

    @Column(name = "ascon_code", length = 50)
    private String asconCode;

    @Column(name = "special_product", length = 255)
    private String specialProduct;

    @Column(name = "item_code", length = 255)
    private String itemCode;

    @Column(name = "supplier_discount", precision = 25, scale = 5)
    private BigDecimal supplierDiscount;

    @Column(name = "cash_discount", length = 10)
    private String cashDiscount;

    @Column(name = "credit_discount", length = 10)
    private String creditDiscount;

    @Column(name = "old_supplier_id", length = 10)
    private String oldSupplierId;

    @Column(name = "warehouse_shelf", length = 50)
    private String warehouseShelf;

    @Column(name = "cash_dis2", length = 10)
    private String cashDis2;

    @Column(name = "cash_dis3", length = 10)
    private String cashDis3;

    @Column(name = "credit_dis2", length = 10)
    private String creditDis2;

    @Column(name = "credit_dis3", length = 10)
    private String creditDis3;

    @Column(name = "group_name", length = 50)
    private String groupName;

    @Column(name = "scientific_name", length = 200)
    private String scientificName;

    @Column(name = "corp_name", length = 200)
    private String corpName;

    @Column(name = "manufacture_country", length = 100)
    private String manufactureCountry;

    @Column(name = "marketing_company", length = 200)
    private String marketingCompany;

    @Column(name = "agent2", length = 200)
    private String agent2;

    @Column(name = "authorized_channel", length = 50)
    private String authorizedChannel;

    @Column(name = "atc_code", length = 50)
    private String atcCode;

    @Column(name = "package_types", length = 50)
    private String packageTypes;

    @Column(name = "legal_status", length = 50)
    private String legalStatus;

    @Column(name = "store_condition", length = 200)
    private String storeCondition;

    @Column(name = "administration_route", length = 50)
    private String administrationRoute;

    @Column(name = "pharmaceutical_form", length = 100)
    private String pharmaceuticalForm;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "variant", length = 200)
    private String variant;

    @Column(name = "product_details_ar", columnDefinition = "TEXT")
    private String productDetailsAr;

    @LegacyBitFlag
    @Column(name = "best_seller")
    private Boolean bestSeller;

    @Column(name = "speciality_id")
    private Integer specialityId;

    @Column(name = "topic_id")
    private Integer topicId;

    @Column(name = "sortby")
    private Integer sortby;

    @Column(name = "new_code", length = 50)
    private String newCode;

    @Column(name = "avenzur_code", length = 100)
    private String avenzurCode;

    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;

    @LegacyBitFlag
    @Column(name = "is_bundle")
    private Boolean isBundle;

    @Column(name = "bundle_items_ids", columnDefinition = "TEXT")
    private String bundleItemsIds;

    @Column(name = "details_ar", columnDefinition = "TEXT")
    private String detailsAr;

    @Column(name = "product_image", length = 2000)
    private String productImage;

    @Column(name = "upc", length = 255)
    private String upc;

    @Column(name = "ean", length = 255)
    private String ean;

    @Column(name = "product_name_external", length = 255)
    private String productNameExternal;

    @Column(name = "external_barcode_url", length = 255)
    private String externalBarcodeUrl;

    @Column(name = "bawazir_image", length = 500)
    private String bawazirImage;

    @Column(name = "bawazir_quantity", precision = 15, scale = 4)
    private BigDecimal bawazirQuantity;

    @Column(name = "shelf_life", length = 255)
    private String shelfLife;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "brand_name", length = 255)
    private String brandName;

    @Column(name = "product_category", length = 255)
    private String productCategory;
}
