package com.avnzor.oms_backend.customers.repository;

import com.avnzor.oms_backend.customers.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findByCompanyId(Integer companyId);

    List<Address> findByIdIn(Collection<Integer> ids);
}
