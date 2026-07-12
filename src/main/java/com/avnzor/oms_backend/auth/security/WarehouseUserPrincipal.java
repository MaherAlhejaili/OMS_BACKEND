package com.avnzor.oms_backend.auth.security;

import com.avnzor.oms_backend.auth.entity.WarehouseWorker;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class WarehouseUserPrincipal implements UserDetails {

    private final Integer id;
    private final String username;
    private final String password;
    private final String name;
    private final String role;
    private final String department;
    private final Collection<? extends GrantedAuthority> authorities;

    public WarehouseUserPrincipal(WarehouseWorker worker) {
        this.id = worker.getId();
        this.username = worker.getUsername();
        this.password = worker.getPassword();
        this.name = worker.getName();
        this.role = worker.getRole();
        this.department = worker.getDepartment();
        this.authorities = buildAuthorities(worker.getRole(), worker.getDepartment());
    }

    private static List<GrantedAuthority> buildAuthorities(String role, String department) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (role != null && !role.isBlank()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase()));
        }

        if (department != null && !department.isBlank()) {
            String normalizedDepartment = department.trim().toUpperCase().replace(' ', '_');
            authorities.add(new SimpleGrantedAuthority("DEPT_" + normalizedDepartment));
        }

        return authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
