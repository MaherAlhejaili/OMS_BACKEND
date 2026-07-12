package com.avnzor.oms_backend.auth.controller;

import com.avnzor.oms_backend.support.AbstractMockMvcIntegrationTest;
import com.avnzor.oms_backend.support.TestUserFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("AuthController API integration tests")
class AuthControllerIT extends AbstractMockMvcIntegrationTest {

    @Test
    @DisplayName("Given valid tenant credentials When logging in Then returns JWT access token")
    void shouldLoginWithValidCredentials() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tenantSlug": "%s",
                                  "username": "logistic.user",
                                  "password": "password"
                                }
                                """.formatted(TestUserFactory.TEST_TENANT_SLUG)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.user.tenantSlug").value(TestUserFactory.TEST_TENANT_SLUG));
    }

    @Test
    @DisplayName("Given invalid password When logging in Then returns unauthorized")
    void shouldRejectInvalidPassword() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tenantSlug": "%s",
                                  "username": "logistic.user",
                                  "password": "wrong-password"
                                }
                                """.formatted(TestUserFactory.TEST_TENANT_SLUG)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Given missing tenant slug When logging in Then returns bad request")
    void shouldValidateLoginRequest() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "logistic.user",
                                  "password": "password"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).contains("tenantSlug");
    }
}
