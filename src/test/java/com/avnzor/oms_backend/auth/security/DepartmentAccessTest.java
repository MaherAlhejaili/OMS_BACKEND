package com.avnzor.oms_backend.auth.security;

import com.avnzor.oms_backend.support.TestUserFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DepartmentAccess unit tests")
class DepartmentAccessTest {

    private final DepartmentAccess departmentAccess = new DepartmentAccess();

    @Test
    @DisplayName("Given logistic user When checking access Then access is granted")
    void shouldAllowLogisticDepartment() {
        var authentication = authenticationFor(TestUserFactory.logisticPrincipal());

        assertThat(departmentAccess.isLogistic(authentication)).isTrue();
    }

    @Test
    @DisplayName("Given warehouse user When checking access Then access is denied")
    void shouldDenyNonLogisticDepartment() {
        var authentication = authenticationFor(TestUserFactory.warehousePrincipal());

        assertThat(departmentAccess.isLogistic(authentication)).isFalse();
    }

    @Test
    @DisplayName("Given null authentication When checking access Then access is denied")
    void shouldDenyNullAuthentication() {
        assertThat(departmentAccess.isLogistic(null)).isFalse();
    }

    private UsernamePasswordAuthenticationToken authenticationFor(WarehouseUserPrincipal principal) {
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }
}
