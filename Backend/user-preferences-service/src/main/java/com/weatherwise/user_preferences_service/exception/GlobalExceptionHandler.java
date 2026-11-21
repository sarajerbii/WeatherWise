package com.weatherwise.user_preferences_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Traduit les exceptions en réponses JSON avec les codes HTTP du contrat d'API. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PreferenceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(PreferenceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MissingUserIdentityException.class)
    public ResponseEntity<ErrorResponse> handleMissingIdentity(MissingUserIdentityException ex,
                                                               HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining(" ; "));
        return build(HttpStatus.BAD_REQUEST, message.isEmpty() ? "Données invalides" : message, request);
    }

    /** JSON mal formé, corps absent ou valeur d'énumération inconnue (ex. temperatureUnit = "KELVIN"). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
                                                          HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "Corps de requête invalide : JSON mal formé ou valeur non reconnue", request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}
