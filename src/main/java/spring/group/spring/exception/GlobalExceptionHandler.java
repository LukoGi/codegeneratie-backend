package spring.group.spring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import spring.group.spring.exception.exceptions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {IncorrectPincodeException.class})
    public ResponseEntity<Object> handleIncorrectPincodeException(IncorrectPincodeException e){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {InsufficientFundsException.class})
    public ResponseEntity<Object> handleInsufficientFundsException(InsufficientFundsException e){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<Object> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<Object> handleMethodNotSupportedException(HttpMessageNotReadableException e){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {NoResourceFoundException.class})
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException e){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {IncorrectFullnameOnCardException.class})
    public ResponseEntity<Object> handleIncorrectFullnameOnCardException(IncorrectFullnameOnCardException e){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e){
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, String> errorsMap = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            errorsMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        String errorMessage = "Validation failed for the following fields: " + errorsMap;

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errorMessage);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}
