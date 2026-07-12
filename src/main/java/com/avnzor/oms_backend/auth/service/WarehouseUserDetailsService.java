package com.avnzor.oms_backend.auth.service;

import com.avnzor.oms_backend.auth.repository.WarehouseWorkerRepository;
import com.avnzor.oms_backend.auth.security.WarehouseUserPrincipal;
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
        return warehouseWorkerRepository.findByUsername(username)
                .map(WarehouseUserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
