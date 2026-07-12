package com.avnzor.oms_backend.tracking.controller;

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

@DisplayName("TrackingController security integration tests")
class TrackingControllerIT extends AbstractMockMvcIntegrationTest {

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("Given authenticated tenant user When listing tracking Then returns success")
    void shouldAllowAuthenticatedUser() throws Exception {
        String token = JwtTestSupport.bearerToken(jwtService, TestUserFactory.warehousePrincipal());

        mockMvc.perform(get("/api/v1/tracking")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message").value("Tracking placeholder"));
    }

    @Test
    @DisplayName("Given no token When listing tracking Then returns unauthorized")
    void shouldRejectAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/v1/tracking"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
