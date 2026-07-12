package com.avnzor.oms_backend.common.config;

import com.avnzor.oms_backend.common.dto.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.avnzor.oms_backend")
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
            @NonNull MethodParameter returnType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response
    ) {
        if (shouldBypassWrapping(request)) {
            return body;
        }

        if (body instanceof ApiResponse) {
            return body;
        }

        if (body instanceof ResponseEntity<?> responseEntity) {
            Object responseBody = responseEntity.getBody();

            if (responseBody instanceof ApiResponse) {
                return body;
            }

            if (responseEntity.getStatusCode().isSameCodeAs(HttpStatus.NO_CONTENT)) {
                return body;
            }

            ApiResponse<Object> wrapped = ApiResponse.success(responseBody);
            return ResponseEntity.status(responseEntity.getStatusCode())
                    .headers(responseEntity.getHeaders())
                    .body(wrapped);
        }

        return ApiResponse.success(body);
    }

    private boolean shouldBypassWrapping(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return path.startsWith("/actuator")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui");
    }
}
