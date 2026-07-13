package com.avnzor.oms_backend.sales.repository;

import com.avnzor.oms_backend.sales.entity.ShopifyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ShopifyOrderRepository extends JpaRepository<ShopifyOrder, Long> {

    Optional<ShopifyOrder> findFirstByOrderNumber(String orderNumber);

    List<ShopifyOrder> findByOrderNumberIn(Collection<String> orderNumbers);
}
