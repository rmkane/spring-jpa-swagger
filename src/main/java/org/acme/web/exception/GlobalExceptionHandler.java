package org.acme.web.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Validation errors occurred")
                .errors(errors)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.debug("Data integrity violation", ex);

        String message = ex.getMessage();
        Throwable rootCause = ex.getRootCause();
        String rootCauseMessage = rootCause != null ? rootCause.getMessage() : null;
        String userMessage = "A conflict occurred with the provided data";

        // Check both the exception message and root cause message
        String fullMessage = (rootCauseMessage != null ? rootCauseMessage + " " : "")
                + (message != null ? message : "");

        // Parse PostgreSQL unique constraint violations
        if (isUniqueConstraintViolation(fullMessage, "users_username_key", "username")) {
            userMessage = "Username already exists";
        } else if (isUniqueConstraintViolation(fullMessage, "users_email_key", "email")) {
            userMessage = "Email already exists";
        } else if (isUniqueConstraintViolation(fullMessage, "books_isbn_key", "isbn")) {
            userMessage = "ISBN already exists";
        } else if (fullMessage.contains("uk_book_author") ||
                (fullMessage.contains("book_authors") && fullMessage.contains("unique"))) {
            userMessage = "This book-author relationship already exists";
        } else if (fullMessage.contains("unique constraint") ||
                fullMessage.contains("duplicate key")) {
            userMessage = "A record with this value already exists";
        }

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(userMessage)
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Checks if the error message indicates a unique constraint violation for a
     * specific constraint or field.
     *
     * @param message        the error message to check
     * @param constraintName the database constraint name (e.g.,
     *                       "users_username_key")
     * @param fieldName      the field name (e.g., "username")
     * @return true if the message indicates a unique constraint violation for the
     *         given constraint or field
     */
    private boolean isUniqueConstraintViolation(String message, String constraintName, String fieldName) {
        return message.contains(constraintName) ||
                (message.contains(fieldName) && message.contains("unique"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
