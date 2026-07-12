package com.avnzor.oms_backend.auth.service;

import com.avnzor.oms_backend.auth.repository.WarehouseWorkerRepository;
import com.avnzor.oms_backend.auth.security.WarehouseUserPrincipal;
import com.avnzor.oms_backend.tenants.context.TenantContextHolder;
import com.avnzor.oms_backend.tenants.exception.TenantContextMissingException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class WarehouseUserDetailsService implements UserDetailsService {

    private final WarehouseWorkerRepository warehouseWorkerRepository;

    public WarehouseUserDetailsService(WarehouseWorkerRepository warehouseWorkerRepository) {
        this.warehouseWorkerRepository = warehouseWorkerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!TenantContextHolder.hasTenant()) {
            throw new TenantContextMissingException();
        }

        return warehouseWorkerRepository.findByUsername(username)
                .map(worker -> new WarehouseUserPrincipal(
                        worker,
                        TenantContextHolder.getTenantId(),
                        TenantContextHolder.getTenantSlug()
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
