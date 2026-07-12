package com.avnzor.oms_backend.orders.controller;

import com.avnzor.oms_backend.auth.service.JwtService;
import com.avnzor.oms_backend.support.AbstractMockMvcIntegrationTest;
import com.avnzor.oms_backend.support.JwtTestSupport;
import com.avnzor.oms_backend.support.TestUserFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("OrderController RBAC integration tests")
class OrderControllerIT extends AbstractMockMvcIntegrationTest {

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("Given logistic department user When listing orders Then returns success")
    void shouldAllowLogisticDepartment() throws Exception {
        String token = JwtTestSupport.bearerToken(jwtService, TestUserFactory.logisticPrincipal());

        mockMvc.perform(get("/api/v1/orders")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message").value("Orders placeholder"));
    }

    @Test
    @DisplayName("Given non-logistic user When listing orders Then returns forbidden")
    void shouldDenyNonLogisticDepartment() throws Exception {
        String token = JwtTestSupport.bearerToken(jwtService, TestUserFactory.warehousePrincipal());

        mockMvc.perform(get("/api/v1/orders")
                        .header("Authorization", token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }
}
