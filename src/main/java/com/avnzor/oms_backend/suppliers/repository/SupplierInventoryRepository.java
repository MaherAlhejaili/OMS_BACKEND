package com.avnzor.oms_backend.suppliers.repository;

import com.avnzor.oms_backend.suppliers.entity.SupplierInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SupplierInventoryRepository extends JpaRepository<SupplierInventory, Integer> {

    List<SupplierInventory> findByProductCodeIn(Collection<String> productCodes);
}
