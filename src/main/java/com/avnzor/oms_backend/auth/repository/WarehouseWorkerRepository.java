package com.avnzor.oms_backend.auth.repository;

import com.avnzor.oms_backend.auth.entity.WarehouseWorker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarehouseWorkerRepository extends JpaRepository<WarehouseWorker, Long> {

    Optional<WarehouseWorker> findByUsername(String username);
}
