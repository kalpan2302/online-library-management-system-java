//package com.olms.backend.exception;
//
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.context.request.WebRequest;
//
//@ControllerAdvice
//public class ApiExceptionHandler {
//
//    @ExceptionHandler(DuplicateResourceException.class)
//    public ResponseEntity<Object> handleDuplicate(DuplicateResourceException ex, WebRequest req) {
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<Object> handleBadRequest(IllegalArgumentException ex, WebRequest req) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
//    }
//
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex, WebRequest req) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//    }
//}

// Abhishek

package com.olms.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

  // ---- 404
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
    return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req.getRequestURI(), null);
  }

  // ---- 409 (duplicate/conflict)
  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex, HttpServletRequest req) {
    return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), req.getRequestURI(), null);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleConflict(IllegalStateException ex, HttpServletRequest req) {
    return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), req.getRequestURI(), null);
  }

  @ExceptionHandler({
      OptimisticLockingFailureException.class,
      PessimisticLockingFailureException.class,
      DataIntegrityViolationException.class
  })
  public ResponseEntity<ErrorResponse> handleDbConflicts(Exception ex, HttpServletRequest req) {
    return build(HttpStatus.CONFLICT, "Conflict", safeMsg(ex), req.getRequestURI(), null);
  }

  // ---- 400 (bad input)
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex, HttpServletRequest req) {
    return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), req.getRequestURI(), null);
  }

  @ExceptionHandler({
      org.springframework.orm.ObjectOptimisticLockingFailureException.class,
      jakarta.persistence.OptimisticLockException.class
  })
  public ResponseEntity<Object> handleOptimistic(Object ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body("Update conflict. Please retry.");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
    var msg = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
        .findFirst().orElse("Validation error");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
      HttpServletRequest req) {
    List<String> errors = new ArrayList<>();
    for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
      errors.add(v.getPropertyPath() + ": " + v.getMessage());
    }
    return build(HttpStatus.BAD_REQUEST, "Validation Failed", "Request validation failed",
        req.getRequestURI(), errors);
  }

  @ExceptionHandler({
      HttpMessageNotReadableException.class, // malformed JSON
      MissingServletRequestParameterException.class, // missing ?param=
      MethodArgumentTypeMismatchException.class // wrong type for path/param
  })
  public ResponseEntity<ErrorResponse> handleRequestFormat(Exception ex, HttpServletRequest req) {
    return build(HttpStatus.BAD_REQUEST, "Bad Request", safeMsg(ex), req.getRequestURI(), null);
  }

  // ---- 401/403 (security)
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException ex, HttpServletRequest req) {
    return build(HttpStatus.UNAUTHORIZED, "Unauthorized", "Authentication required", req.getRequestURI(), null);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleForbidden(AccessDeniedException ex, HttpServletRequest req) {
    return build(HttpStatus.FORBIDDEN, "Forbidden", "Insufficient privileges", req.getRequestURI(), null);
  }

  // ---- 500 (fallback)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest req) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
        "Something went wrong", req.getRequestURI(), null);
  }

  // ---- helpers
  private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message,
      String path, List<String> errors) {
    ErrorResponse body = new ErrorResponse(
        Instant.now().toString(),
        status.value(),
        error,
        message,
        path,
        errors == null || errors.isEmpty() ? null : errors);
    return ResponseEntity.status(status).body(body);
  }

  // snigdha
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {

    // convert HttpStatusCode to HttpStatus (or fallback to INTERNAL_SERVER_ERROR)
    HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
    if (status == null)
      status = HttpStatus.INTERNAL_SERVER_ERROR;

    // build the ErrorResponse
    return build(
        status,
        status.getReasonPhrase(), // e.g., "Unauthorized"
        ex.getReason(), // your custom message like "Invalid credentials"
        req.getRequestURI(),
        null);
  }

  /** avoid leaking DB internals; fall back to simple message */
  private String safeMsg(Exception ex) {
    String m = ex.getMessage();
    return (m == null || m.isBlank()) ? ex.getClass().getSimpleName() : m;
  }

  public static class ErrorResponse {
    private final String timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<String> errors;

    public ErrorResponse(String timestamp, int status, String error, String message,
        String path, List<String> errors) {
      this.timestamp = timestamp;
      this.status = status;
      this.error = error;
      this.message = message;
      this.path = path;
      this.errors = errors;
    }

    public String getTimestamp() {
      return timestamp;
    }

    public int getStatus() {
      return status;
    }

    public String getError() {
      return error;
    }

    public String getMessage() {
      return message;
    }

    public String getPath() {
      return path;
    }

    public List<String> getErrors() {
      return errors;
    }
  }
}
