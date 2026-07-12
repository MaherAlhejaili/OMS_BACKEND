package com.avnzor.oms_backend.suppliers.repository;

import com.avnzor.oms_backend.suppliers.entity.SupplierOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SupplierOrderRepository extends JpaRepository<SupplierOrder, Integer> {

    Optional<SupplierOrder> findFirstByOrderId(Integer orderId);

    List<SupplierOrder> findByOrderIdIn(Collection<Integer> orderIds);
}
