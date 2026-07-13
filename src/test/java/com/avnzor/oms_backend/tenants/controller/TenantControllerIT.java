package com.avnzor.oms_backend.tenants.controller;

import com.avnzor.oms_backend.auth.service.JwtService;
import com.avnzor.oms_backend.support.AbstractMockMvcIntegrationTest;
import com.avnzor.oms_backend.support.JwtTestSupport;
import com.avnzor.oms_backend.support.TestUserFactory;
import com.avnzor.oms_backend.tenants.security.PlatformUserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("TenantController platform API integration tests")
class TenantControllerIT extends AbstractMockMvcIntegrationTest {

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("Given valid platform credentials When logging in Then returns JWT access token")
    void shouldLoginWithValidPlatformCredentials() throws Exception {
        mockMvc.perform(post("/api/v1/platform/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "platform.admin",
                                  "password": "admin123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value("platform.admin"));
    }

    @Test
    @DisplayName("Given invalid platform password When logging in Then returns unauthorized")
    void shouldRejectInvalidPlatformPassword() throws Exception {
        mockMvc.perform(post("/api/v1/platform/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "platform.admin",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Given platform admin token When listing tenants Then returns success")
    void shouldAllowPlatformAdminToListTenants() throws Exception {
        String token = platformAdminToken();

        mockMvc.perform(get("/api/v1/platform/tenants")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].slug").value(TestUserFactory.TEST_TENANT_SLUG));
    }

    @Test
    @DisplayName("Given tenant user token When listing tenants Then returns forbidden")
    void shouldDenyTenantUserFromListingTenants() throws Exception {
        String token = JwtTestSupport.bearerToken(jwtService, TestUserFactory.logisticPrincipal());

        mockMvc.perform(get("/api/v1/platform/tenants")
                        .header("Authorization", token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Given no token When listing tenants Then returns unauthorized")
    void shouldRejectAnonymousPlatformAccess() throws Exception {
        mockMvc.perform(get("/api/v1/platform/tenants"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    private String platformAdminToken() {
        PlatformUserPrincipal principal = new PlatformUserPrincipal(
                platformAdminUser()
        );
        return JwtTestSupport.platformBearerToken(jwtService, principal);
    }

    private com.avnzor.oms_backend.tenants.entity.PlatformUser platformAdminUser() {
        com.avnzor.oms_backend.tenants.entity.PlatformUser user =
                new com.avnzor.oms_backend.tenants.entity.PlatformUser();
        user.setId(1L);
        user.setUsername("platform.admin");
        user.setPassword("encoded");
        user.setName("Platform Administrator");
        user.setRole("PLATFORM_ADMIN");
        user.setActive(true);
        return user;
    }
}
