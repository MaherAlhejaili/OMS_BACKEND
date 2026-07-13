package com.avnzor.oms_backend.common.exception;

import com.avnzor.oms_backend.auth.util.JwtErrorMessages;
import com.avnzor.oms_backend.common.dto.ApiResponse;
import com.avnzor.oms_backend.tenants.exception.TenantContextMissingException;
import com.avnzor.oms_backend.tenants.exception.TenantDisabledException;
import com.avnzor.oms_backend.tenants.exception.TenantNotFoundException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TenantNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleTenantNotFound(
            TenantNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler({TenantDisabledException.class, TenantContextMissingException.class})
    public ResponseEntity<ApiResponse<Void>> handleTenantBadRequest(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(
            UnauthorizedException exception,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.UNAUTHORIZED, exception.getMessage(), request);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtException(
            JwtException exception,
            HttpServletRequest request
    ) {
        log.debug("Invalid JWT on path {}: {}", request.getRequestURI(), exception.getMessage());
        return buildError(HttpStatus.UNAUTHORIZED, JwtErrorMessages.resolve(exception), request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(
            BadRequestException exception,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.FORBIDDEN, "Access denied", request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                extractPath(request),
                fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return toObjectResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                methodNotAllowedMessage(exception),
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return toObjectResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Content type is not supported for this endpoint",
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return toObjectResponse(
                HttpStatus.NOT_ACCEPTABLE,
                "Requested response content type is not acceptable",
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return toObjectResponse(
                HttpStatus.BAD_REQUEST,
                "Required request parameter '%s' is missing".formatted(exception.getParameterName()),
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return toObjectResponse(
                HttpStatus.BAD_REQUEST,
                "Malformed request body",
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return toObjectResponse(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request
    ) {
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        if (status.is5xxServerError()) {
            log.error("Unexpected error on path {}", extractPath(request), exception);
            body = ApiResponse.error(
                    status.value(),
                    "An unexpected error occurred",
                    extractPath(request)
            );
        } else if (!(body instanceof ApiResponse)) {
            String message = exception.getMessage() == null ? status.getReasonPhrase() : exception.getMessage();
            body = ApiResponse.error(status.value(), message, extractPath(request));
        }

        return super.handleExceptionInternal(exception, body, headers, statusCode, request);
    }

    private ResponseEntity<Object> toObjectResponse(
            HttpStatus status,
            String message,
            WebRequest request
    ) {
        ApiResponse<Void> body = ApiResponse.error(
                status.value(),
                message,
                extractPath(request)
        );
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<ApiResponse<Void>> buildError(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        ApiResponse<Void> response = ApiResponse.error(
                status.value(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(response);
    }

    private String extractPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return null;
    }

    private String methodNotAllowedMessage(HttpRequestMethodNotSupportedException exception) {
        String method = exception.getMethod();
        Set<?> supportedMethods = exception.getSupportedHttpMethods();
        if (supportedMethods == null || supportedMethods.isEmpty()) {
            return "HTTP method '%s' is not supported for this endpoint".formatted(method);
        }

        String supported = supportedMethods.stream()
                .map(Object::toString)
                .sorted()
                .collect(Collectors.joining(", "));

        return "HTTP method '%s' is not supported for this endpoint. Supported methods: %s"
                .formatted(method, supported);
    }
}
