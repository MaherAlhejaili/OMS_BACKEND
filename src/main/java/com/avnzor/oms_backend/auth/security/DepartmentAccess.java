package com.avnzor.oms_backend.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("departmentAccess")
public class DepartmentAccess {

    public boolean isLogistic(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof WarehouseUserPrincipal user)) {
            return false;
        }

        String department = user.getDepartment();
        if (department == null || department.isBlank()) {
            return false;
        }

        String normalized = department.trim().toLowerCase();
        return normalized.equals("logistic")
                || normalized.equals("logistics")
                || normalized.equals("logistic team");
    }
}
