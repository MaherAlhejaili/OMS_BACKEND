package com.avnzor.oms_backend.customers.repository;

import com.avnzor.oms_backend.customers.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
}
