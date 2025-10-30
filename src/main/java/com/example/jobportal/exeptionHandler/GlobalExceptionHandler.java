package com.example.jobportal.exeptionHandler;

import com.example.jobportal.exeptionHandler.customException.*;
import com.example.jobportal.exeptionHandler.dto.ErrorMessage;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorMessage> duplicateKeyException(DuplicateKeyException e) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .message(e.getMessage())
                .status(HttpStatus.CONFLICT)
                .timestamp(new Date(System.currentTimeMillis())).build();
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentials.class)
    public ResponseEntity<String> handleInvalidCredentials(InvalidCredentials ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CompanyNotFound.class)
    public ResponseEntity<ErrorMessage> handleCompanyNotFound(CompanyNotFound ex){
        ErrorMessage errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(new Date(System.currentTimeMillis()))
                .build();
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CompanyAlreadyRegisterByUser.class)
    public ResponseEntity<ErrorMessage> handleCompanyAlreadyRegisterByUser(CompanyAlreadyRegisterByUser ex){

        ErrorMessage errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT)
                .timestamp(new Date(System.currentTimeMillis()))
                .build();
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CandidateProfileAlreadyCreated.class)
    public ResponseEntity<ErrorMessage> handleCandidateProfileAlreadyCreated(CandidateProfileAlreadyCreated e){
        ErrorMessage errorMessage = ErrorMessage.builder()
                .message(e.getMessage())
                .status(HttpStatus.CONFLICT)
                .timestamp(new Date(System.currentTimeMillis())).build();
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ApplicationAlreadySubmited.class)
    public ResponseEntity<ErrorMessage> handleApplicationAlreadySubmitted(ApplicationAlreadySubmited e){
        ErrorMessage errorMessage = ErrorMessage.builder()
                .message(e.getMessage())
                .status(HttpStatus.CONFLICT)
                .timestamp(new Date(System.currentTimeMillis())).build();
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }
}
