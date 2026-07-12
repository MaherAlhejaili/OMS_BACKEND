package com.avnzor.oms_backend.sales.repository;

import com.avnzor.oms_backend.sales.entity.ShopifyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopifyOrderRepository extends JpaRepository<ShopifyOrder, Integer> {

    Optional<ShopifyOrder> findFirstByOrderNumber(String orderNumber);
}
