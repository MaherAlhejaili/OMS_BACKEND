package com.avnzor.oms_backend.sales.repository;

import com.avnzor.oms_backend.sales.entity.SaleStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleStatusHistoryRepository extends JpaRepository<SaleStatusHistory, Integer> {
}
