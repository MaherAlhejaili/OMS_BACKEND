package com.avnzor.oms_backend.shipping.repository;

import com.avnzor.oms_backend.shipping.entity.ShipmentCreationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ShipmentCreationLogRepository extends JpaRepository<ShipmentCreationLog, Integer> {

    List<ShipmentCreationLog> findByOrderIdOrderByCreatedAtAsc(Integer orderId);

    List<ShipmentCreationLog> findByTrackingNumberIn(Collection<String> trackingNumbers);
}
