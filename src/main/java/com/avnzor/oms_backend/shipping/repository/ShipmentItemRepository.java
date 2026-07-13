package com.avnzor.oms_backend.shipping.repository;

import com.avnzor.oms_backend.shipping.entity.ShipmentItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, Integer> {

    List<ShipmentItem> findByOrderId(Integer orderId);

    List<ShipmentItem> findByOrderIdIn(Collection<Integer> orderIds);
}
