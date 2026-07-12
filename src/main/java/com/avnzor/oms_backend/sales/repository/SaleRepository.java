package com.avnzor.oms_backend.sales.repository;

import com.avnzor.oms_backend.sales.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Integer> {

    Optional<Sale> findByCourierOrderTrackingId(String trackingId);
}
