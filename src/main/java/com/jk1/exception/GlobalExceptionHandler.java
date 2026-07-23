package com.jk1.exception;

import com.jk1.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LazyInitializationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Centralised exception handling for the JKØ application.
 *
 * <p><strong>MVC vs REST routing</strong><br>
 * Spring MVC controllers (returning view names) and REST/JSON endpoints share
 * the same {@code @ControllerAdvice}. The handler detects whether a request
 * wants HTML or JSON by inspecting the {@code Accept} header:</p>
 * <ul>
 *   <li>If the request accepts {@code text/html} (browser navigation), a
 *       Thymeleaf error view ({@code error/404} or {@code error/500}) is
 *       returned as a {@link ModelAndView}.</li>
 *   <li>If the request accepts {@code application/json} (AJAX / API calls),
 *       a {@link ResponseEntity} with an {@link ErrorResponseDTO} body is
 *       returned.</li>
 * </ul>
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // ─────────────────────────────────────────────────────────────────────────
    // Domain-specific exceptions
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Handles {@link ResourceNotFoundException} and its subclasses
     * ({@link ProductNotFoundException}, {@link CategoryNotFoundException},
     * {@link OrderNotFoundException}, {@link UserNotFoundException}).
     *
     * <p>Browser requests get the {@code error/404} Thymeleaf view.
     * JSON requests get a 404 JSON body.</p>
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request,
            Model model) {

        log.warn("[404] {} — path: {}", ex.getMessage(), request.getRequestURI());

        if (wantsJson(request)) {
            return buildJsonResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
        }

        return buildMvcError(HttpStatus.NOT_FOUND,
                "Page Not Found",
                ex.getMessage(),
                "error/404");
    }

    /**
     * Handles duplicate resource creation attempts (e.g., duplicate email,
     * duplicate SKU). Always returns JSON because these originate from form
     * submissions that are handled via REST in this project.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateResourceException(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        log.warn("[409] {} — path: {}", ex.getMessage(), request.getRequestURI());
        return buildJsonResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    /**
     * Handles invalid business operation exceptions.
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidOperationException(
            InvalidOperationException ex,
            HttpServletRequest request) {

        log.warn("[400] {} — path: {}", ex.getMessage(), request.getRequestURI());
        return buildJsonResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /**
     * Handles Jakarta Validation failures on {@code @Valid}-annotated
     * request bodies (REST endpoints only).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("[400] Validation failed — {}", errors);
        return buildJsonResponse(HttpStatus.BAD_REQUEST, errors, request);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Infrastructure exceptions
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Catches {@link LazyInitializationException} — the "no session" error from
     * Hibernate when a lazy relation is accessed after the persistence context
     * closes. This should not happen if all service reads are
     * {@code @Transactional(readOnly=true)} and repositories use JOIN FETCH, but
     * this handler acts as the last safety net.
     */
    @ExceptionHandler(LazyInitializationException.class)
    public Object handleLazyInitializationException(
            LazyInitializationException ex,
            HttpServletRequest request) {

        log.error("[500] LazyInitializationException at {}: {}", request.getRequestURI(), ex.getMessage());

        if (wantsJson(request)) {
            return buildJsonResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Data loading error — please try again.", request);
        }

        return buildMvcError(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something Went Wrong",
                "A data loading error occurred. Please refresh or go back.",
                "error/500");
    }

    /**
     * Handles database constraint violations (e.g. duplicate keys).
     */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public Object handleDataIntegrityViolationException(
            org.springframework.dao.DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.warn("[409] Database constraint violation at {}: {}", request.getRequestURI(), ex.getMessage());

        if (wantsJson(request)) {
            return buildJsonResponse(HttpStatus.CONFLICT,
                    "A conflict occurred saving data. Please ensure data is unique and try again.", request);
        }

        return buildMvcError(HttpStatus.CONFLICT,
                "Data Conflict",
                "The data you submitted conflicts with existing records.",
                "error/500");
    }

    /**
     * Handles JPA validation constraints before flushing to DB.
     */
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public Object handleConstraintViolationException(
            jakarta.validation.ConstraintViolationException ex,
            HttpServletRequest request) {

        log.warn("[400] Constraint violation at {}: {}", request.getRequestURI(), ex.getMessage());

        if (wantsJson(request)) {
            return buildJsonResponse(HttpStatus.BAD_REQUEST,
                    "Invalid data format. Please correct and try again.", request);
        }

        return buildMvcError(HttpStatus.BAD_REQUEST,
                "Bad Request",
                "The data you submitted was invalid.",
                "error/500");
    }

    /**
     * Handles missing static resources (like images) and undefined endpoints.
     * Prevents these from falling through to the generic 500 error handler.
     */
    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public Object handleNotFoundException(Exception ex, HttpServletRequest request) {
        log.warn("[404] {} — path: {}", ex.getMessage(), request.getRequestURI());

        // Fallback for missing images
        if (request.getRequestURI().startsWith("/images/")) {
            return new ModelAndView("redirect:https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?q=80&w=600&auto=format&fit=crop");
        }

        if (wantsJson(request)) {
            return buildJsonResponse(HttpStatus.NOT_FOUND, "Resource not found", request);
        }

        return buildMvcError(HttpStatus.NOT_FOUND,
                "Page Not Found",
                "The requested resource was not found.",
                "error/404");
    }

    /**
     * Catch-all handler for any unhandled exception.
     * Returns the Thymeleaf 500 page for browser requests and JSON for API calls.
     */
    @ExceptionHandler(Exception.class)
    public Object handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("[500] Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        if (wantsJson(request)) {
            return buildJsonResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred. Please try again.", request);
        }

        return buildMvcError(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something Went Wrong",
                "An unexpected error occurred. Our team has been notified.",
                "error/500");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} when the caller explicitly wants JSON
     * (e.g., AJAX / REST API calls with {@code Accept: application/json}).
     * Browser navigations send {@code Accept: text/html, *\/*} so this returns false.
     */
    private boolean wantsJson(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null
                && accept.contains("application/json")
                && !accept.contains("text/html");
    }

    /**
     * Builds a {@link ResponseEntity} carrying an {@link ErrorResponseDTO} body.
     */
    private ResponseEntity<ErrorResponseDTO> buildJsonResponse(
            HttpStatus status, String message, HttpServletRequest request) {

        ErrorResponseDTO body = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(body, status);
    }

    /**
     * Builds a {@link ModelAndView} that renders a Thymeleaf error template.
     *
     * @param status      HTTP status to set on the response
     * @param title       short title shown on the error page
     * @param description longer description shown on the error page
     * @param viewName    Thymeleaf view name, e.g. {@code "error/404"}
     */
    private ModelAndView buildMvcError(
            HttpStatus status, String title, String description, String viewName) {

        ModelAndView mav = new ModelAndView(viewName);
        mav.setStatus(status);
        mav.addObject("errorTitle",       title);
        mav.addObject("errorDescription", description);
        mav.addObject("statusCode",       status.value());
        return mav;
    }
}
