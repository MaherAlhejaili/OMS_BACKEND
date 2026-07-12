package com.avnzor.oms_backend.products.repository;

import com.avnzor.oms_backend.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findByCode(String code);

    List<Product> findByCodeIn(Collection<String> codes);
}
