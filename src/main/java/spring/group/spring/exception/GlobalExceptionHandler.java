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
import org.springframework.security.access.AccessDeniedException;
import spring.group.spring.exception.exceptions.*;
import spring.group.spring.models.dto.ApiError;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// K - Exception handling
@ControllerAdvice
public class GlobalExceptionHandler {

    // Start all Bad Request Error handling here
    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {InsufficientFundsException.class})
    public ResponseEntity<Object> handleInsufficientFundsException(InsufficientFundsException e){
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<Object> handleMethodNotSupportedException(HttpMessageNotReadableException e){
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AbsoluteLimitExceededException.class})
    public ResponseEntity<Object> handleAbsoluteTransferLimitHitException(AbsoluteLimitExceededException e){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {DailyTransferLimitExceededException.class})
    public ResponseEntity<Object> handleDailyTransferLimitHitException(DailyTransferLimitExceededException e){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {TransactionWithSavingsAccountException.class})
    public ResponseEntity<Object> handleTransactionWithSavingsAccountException(TransactionWithSavingsAccountException e) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {ActiveCheckingAccountNotFoundException.class})
    public ResponseEntity<Object> handleActiveCheckingAccountNotFoundException(ActiveCheckingAccountNotFoundException e) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    /**
     * MethodArgumentNotValidException
     * The format of the error message is as follows:
     * {
     *   "status": "BAD_REQUEST",
     *   "message": "Validation failed for the following fields: {fieldName1=errorMessage1, fieldName2=errorMessage2, ...}"
     * }
     * example message:
     * "message": "Validation failed for the following fields: {password=must not be blank, username=must not be blank}"
     */
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

    // Start all Method Not Allowed Error handling here
    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<Object> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Start all Not Found Error handling here
    @ExceptionHandler(value = {NoResourceFoundException.class})
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException e){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    // Start all Forbidden Error handling here
    @ExceptionHandler(value = {IncorrectFullNameOnCardException.class})
    public ResponseEntity<Object> handleIncorrectfullNameOnCardException(IncorrectFullNameOnCardException e){
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {IncorrectIbanException.class})
    public ResponseEntity<Object> handleIncorrectIbanException(IncorrectIbanException e){
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {IncorrectPinCodeException.class})
    public ResponseEntity<Object> handleIncorrectPinCodeException(IncorrectPinCodeException e){
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, e.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e){
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    // Start all Internal Server Error handling here

    // Last resort exception handler
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleException(){
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
