package com.sportstore.backend.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
    return ResponseEntity.status(ex.getStatusCode())
      .body(Map.of(
        "success", false,
        "message", ex.getReason() == null ? "Request failed" : ex.getReason()
      ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(Map.of(
        "success", false,
        "message", "Unexpected server error"
      ));
  }
}
