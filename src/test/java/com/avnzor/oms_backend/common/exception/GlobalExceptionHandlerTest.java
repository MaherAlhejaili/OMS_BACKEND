package com.avnzor.oms_backend.common.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.ServletWebRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler web error tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Given unsupported HTTP method When handled Then returns 405 ApiResponse")
    void shouldReturnMethodNotAllowedForWrongHttpMethod() {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest("POST", "/api/v1/sales/lookup");
        ServletWebRequest webRequest = new ServletWebRequest(servletRequest);

        ResponseEntity<Object> response = handler.handleHttpRequestMethodNotSupported(
                new HttpRequestMethodNotSupportedException("POST", java.util.List.of("GET")),
                new org.springframework.http.HttpHeaders(),
                HttpStatus.METHOD_NOT_ALLOWED,
                webRequest
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isInstanceOf(com.avnzor.oms_backend.common.dto.ApiResponse.class);

        @SuppressWarnings("unchecked")
        var body = (com.avnzor.oms_backend.common.dto.ApiResponse<Void>) response.getBody();
        assertThat(body.success()).isFalse();
        assertThat(body.status()).isEqualTo(405);
        assertThat(body.message()).contains("POST");
        assertThat(body.message()).contains("GET");
        assertThat(body.path()).isEqualTo("/api/v1/sales/lookup");
    }
}
