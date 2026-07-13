package com.avnzor.oms_backend.sales.repository;

import com.avnzor.oms_backend.sales.entity.SalesJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SalesJobRepository extends JpaRepository<SalesJob, Integer> {

    Optional<SalesJob> findFirstByReferenceNo(String referenceNo);

    List<SalesJob> findByReferenceNoIn(Collection<String> referenceNos);
}
