package com.backspark.socks.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InsufficientSocksException.class)
    public ResponseEntity<ApiError> handleInsufficientSocksException(InsufficientSocksException ex) {
        logger.warn("Insufficient socks: {}", ex.getMessage());
        ApiError apiError = new ApiError(ex.getMessage(), "Check the stock availability for the requested socks.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ApiError> handleFileProcessingException(FileProcessingException ex) {
        logger.error("File processing error: {}", ex.getMessage(), ex);
        ApiError apiError = new ApiError(ex.getMessage(), "Ensure the uploaded file is valid and meets the requirements.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        logger.warn("Validation error: {}", errorMessage);
        ApiError apiError = new ApiError("Validation failed", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex) {
        logger.error("Unhandled exception: {}", ex.getMessage(), ex);
        ApiError apiError = new ApiError("An unexpected error occurred.", ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }
}
