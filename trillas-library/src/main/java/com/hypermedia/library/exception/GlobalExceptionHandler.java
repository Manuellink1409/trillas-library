package com.hypermedia.library.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp){
        return ResponseEntity.status(UNAUTHORIZED).body(ExceptionResponse
                        .builder()
                        .businessErrorCode(BusinessErrorCode.ACCOUNT_LOCKED.getCode())
                        .businessErrorDescription(BusinessErrorCode.ACCOUNT_LOCKED.getDescription())
                        .error(exp.getMessage())
                        .build());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp){
        return ResponseEntity.status(UNAUTHORIZED).body(ExceptionResponse
                .builder()
                .businessErrorCode(BusinessErrorCode.ACCOUNT_DISABLED.getCode())
                .businessErrorDescription(BusinessErrorCode.ACCOUNT_DISABLED.getDescription())
                .error(exp.getMessage())
                .build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(){
        return ResponseEntity.status(UNAUTHORIZED).body(ExceptionResponse
                .builder()
                .businessErrorCode(BusinessErrorCode.BAD_CREDENTIALS.getCode())
                .businessErrorDescription(BusinessErrorCode.BAD_CREDENTIALS.getDescription())
                .error(BusinessErrorCode.BAD_CREDENTIALS.getDescription())
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp){
        Set<String> errors = new HashSet<>();
        exp.getBindingResult()
                .getAllErrors()
                .forEach(error->{
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });
        return ResponseEntity.status(BAD_REQUEST).body(ExceptionResponse
                .builder()
                .validationErrors(errors)
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp){
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ExceptionResponse
                .builder()
                .businessErrorDescription("Internal error, contact the admin")
                .error(exp.getMessage())
                .build());
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exp){
        return ResponseEntity.status(BAD_REQUEST).body(ExceptionResponse
                .builder()
                .error(exp.getMessage())
                .build());
    }

}
