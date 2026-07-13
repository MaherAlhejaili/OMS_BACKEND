package com.avnzor.oms_backend.customers.repository;

import com.avnzor.oms_backend.customers.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

    List<Company> findByIdIn(Collection<Integer> ids);
}
