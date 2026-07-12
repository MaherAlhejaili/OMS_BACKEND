package com.avnzor.oms_backend.tenants.service;

import com.avnzor.oms_backend.tenants.repository.PlatformUserRepository;
import com.avnzor.oms_backend.tenants.security.PlatformUserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PlatformUserDetailsService implements UserDetailsService {

    private final PlatformUserRepository platformUserRepository;

    public PlatformUserDetailsService(PlatformUserRepository platformUserRepository) {
        this.platformUserRepository = platformUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return platformUserRepository.findByUsername(username)
                .filter(user -> user.isActive())
                .map(PlatformUserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("Platform user not found: " + username));
    }
}
