package com.avnzor.oms_backend.sales.repository;

import com.avnzor.oms_backend.sales.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {

    @Query("SELECT si FROM SaleItem si WHERE si.saleId = :saleId")
    List<SaleItem> findBySaleId(@Param("saleId") Integer saleId);

    @Query("SELECT si FROM SaleItem si WHERE si.saleId = :saleId AND si.productCode = :productCode")
    Optional<SaleItem> findBySaleIdAndProductCode(
            @Param("saleId") Integer saleId,
            @Param("productCode") String productCode
    );

    @Query("SELECT si FROM SaleItem si WHERE si.saleId = :saleId AND si.productCode IN :productCodes")
    List<SaleItem> findBySaleIdAndProductCodeIn(
            @Param("saleId") Integer saleId,
            @Param("productCodes") List<String> productCodes
    );
}
