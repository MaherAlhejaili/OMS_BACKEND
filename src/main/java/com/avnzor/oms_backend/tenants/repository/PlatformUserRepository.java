package com.avnzor.oms_backend.tenants.repository;

import com.avnzor.oms_backend.tenants.entity.PlatformUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlatformUserRepository extends JpaRepository<PlatformUser, Long> {

    Optional<PlatformUser> findByUsername(String username);
}
