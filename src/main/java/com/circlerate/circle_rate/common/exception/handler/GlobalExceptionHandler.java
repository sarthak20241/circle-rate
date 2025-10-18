package com.circlerate.circle_rate.common.exception.handler;

import com.circlerate.circle_rate.common.exception.body.ErrorMessage;
import com.circlerate.circle_rate.common.exception.custom_exception.InterestAlreadyExistsException;
import com.circlerate.circle_rate.common.exception.custom_exception.InterestNotFoundException;
import com.circlerate.circle_rate.common.exception.custom_exception.PropertyAlreadyExistsException;
import com.circlerate.circle_rate.common.exception.custom_exception.PropertyNotFoundException;
import com.circlerate.circle_rate.common.exception.custom_exception.PropertyTypeRequiredException;
import com.circlerate.circle_rate.common.exception.custom_exception.UserAlreadyExistsException;
import com.circlerate.circle_rate.common.exception.custom_exception.UserNotAuthorizedException;
import com.circlerate.circle_rate.common.exception.custom_exception.UserNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = PropertyNotFoundException.class)
    public ResponseEntity<ErrorMessage> propertyNotFoundException(PropertyNotFoundException ex){
        ErrorMessage message = new ErrorMessage(HttpStatus.NOT_FOUND.value(),ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(message);
    }
    
    @ExceptionHandler(value = PropertyAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> propertyAlreadyExistsException(PropertyAlreadyExistsException ex){
        ErrorMessage message = new ErrorMessage(HttpStatus.CONFLICT.value(),ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(message);
    }
    
    @ExceptionHandler(value = PropertyTypeRequiredException.class)
    public ResponseEntity<ErrorMessage> propertyTypeRequiredException(PropertyTypeRequiredException ex){
        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST.value(),ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(message);
    }

    @ExceptionHandler(value = UserAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> userAlreadyExistsException(UserAlreadyExistsException ex){
        ErrorMessage message = new ErrorMessage(HttpStatus.CONFLICT.value(),ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(message);
    }

    //exception handler for @valid checks
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), errorMsg);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessage> handleConstraintViolation(ConstraintViolationException ex) {
        String errorMsg = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse("Validation failed");

        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), errorMsg);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> userNotFoundException(UserNotFoundException ex){
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
        String userId = null;
        if (authentication != null) {
            userId = authentication.getName(); 
        }
        log.error("User not found with userId: {}", userId);
        ErrorMessage message = new ErrorMessage(HttpStatus.NOT_FOUND.value(),ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(message);
    }

    @ExceptionHandler(value = UserNotAuthorizedException.class)
    public ResponseEntity<ErrorMessage> userNotAuthorizedException(UserNotAuthorizedException ex){
        ErrorMessage message = new ErrorMessage(HttpStatus.UNAUTHORIZED.value(),ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(message);
    }
    
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> illegalArgumentException(IllegalArgumentException ex){
        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST.value(),ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(message);
    }
    
    @ExceptionHandler(value = InterestAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> interestAlreadyExistsException(InterestAlreadyExistsException ex){
        ErrorMessage message = new ErrorMessage(HttpStatus.CONFLICT.value(),ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(message);
    }
    
    @ExceptionHandler(value = InterestNotFoundException.class)
    public ResponseEntity<ErrorMessage> interestNotFoundException(InterestNotFoundException ex){
        ErrorMessage message = new ErrorMessage(HttpStatus.NOT_FOUND.value(),ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(message);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> httpMessageNotReadableException(HttpMessageNotReadableException ex){
        String errorMessage = "Invalid JSON format or enum value. Please check your request data.";

        if (ex.getMessage() != null && ex.getMessage().contains("not one of the values accepted for Enum")) {
            errorMessage = "Invalid enum value provided. Please check the allowed values for the field.";
        }

        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(message);
    }
}
