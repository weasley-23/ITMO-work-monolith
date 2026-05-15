package com.itmo_work.api_monolith.exception;

import com.itmo_work.api_monolith.exception.exceptions.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CurrencyNotFoundException.class)
    public ResponseEntity<ProblemDetail> CurrencyNotFoundException(CurrencyNotFoundException e, HttpServletRequest request) {
        ProblemDetail body = ProblemDetailsUtils.problemDetail(HttpStatus.BAD_REQUEST, e.getMessage(), "", request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(InvalidVacancyStatusException.class)
    public ResponseEntity<ProblemDetail> InvalidVacancyStatusException(InvalidVacancyStatusException e, HttpServletRequest request) {
        ProblemDetail body = ProblemDetailsUtils.problemDetail(HttpStatus.BAD_REQUEST, "Update available only for DRAFT or PUBLISHED vacancies", "", request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(InvalidVacancySalaryException.class)
    public ResponseEntity<ProblemDetail> InvalidVacancySalaryException(InvalidVacancySalaryException e, HttpServletRequest request) {
        ProblemDetail body = ProblemDetailsUtils.problemDetail(HttpStatus.BAD_REQUEST, e.getMessage(), "", request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(InvalidVacancyStatusChangeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidStatusChange(InvalidVacancyStatusChangeException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyExistsException(UserAlreadyExistsException e, HttpServletRequest request) {
        ProblemDetail body = ProblemDetailsUtils.problemDetail(HttpStatus.BAD_REQUEST, "User already exists", "", request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyExistsException(UserNotFoundException e, HttpServletRequest request) {
        ProblemDetail body = ProblemDetailsUtils.problemDetail(HttpStatus.BAD_REQUEST, "User not found", "", request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        ProblemDetail body = ProblemDetailsUtils.problemDetail(HttpStatus.BAD_REQUEST, e.getMessage(), "", request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    @ExceptionHandler(VacancyStatusNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleVacancyStatusNotFoundException(VacancyStatusNotFoundException e, HttpServletRequest request) {
        ProblemDetail body = ProblemDetailsUtils.problemDetail(HttpStatus.BAD_REQUEST, "Vacancy status was not found", "", request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(VacancyNotOpenedException.class)
    public ResponseEntity<ProblemDetail> handleVacancyNotOpenedException(VacancyNotOpenedException e, HttpServletRequest request) {
        ProblemDetail body = ProblemDetailsUtils.problemDetail(HttpStatus.NOT_FOUND, "Vacancy is not opened", "", request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(VacancyNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleVacancyNotFoundException(VacancyNotFoundException e, HttpServletRequest request) {
        ProblemDetail body = ProblemDetailsUtils.problemDetail(HttpStatus.NOT_FOUND, "Vacancy does not exist", "", request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UserHasAlreadyAppliedException.class)
    public ResponseEntity<ProblemDetail> handleUserHasAlreadyAppliedException(UserHasAlreadyAppliedException e, HttpServletRequest request) {
        ProblemDetail body = ProblemDetailsUtils.problemDetail(HttpStatus.BAD_REQUEST, "User has already applied for current vacancy", "", request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ApplicationStatusNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleApplicationStatusNotFoundException(ApplicationStatusNotFoundException e, HttpServletRequest request) {
        ProblemDetail body = ProblemDetailsUtils.problemDetail(HttpStatus.BAD_REQUEST, "Application status was not found", "", request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
    @ExceptionHandler(CompanyAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleCompanyAlreadyExistsException(CompanyAlreadyExistsException e, HttpServletRequest request ){
        ProblemDetail body = ProblemDetailsUtils.problemDetail(HttpStatus.CONFLICT, e.getMessage(), "", request);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleApplicationNotFoundException(ApplicationNotFoundException e, HttpServletRequest request) {
        ProblemDetail body = ProblemDetailsUtils.problemDetail(HttpStatus.BAD_REQUEST, "Application was not found", "", request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleApplicationNotFoundException(RoleNotFoundException e, HttpServletRequest request) {
        ProblemDetail body = ProblemDetailsUtils.problemDetail(HttpStatus.BAD_REQUEST, "User role was not found", "", request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UserDoesNotBelongToCompanyException.class)
    public ResponseEntity<ProblemDetail> handleApplicationNotFoundException(UserDoesNotBelongToCompanyException e, HttpServletRequest request) {
        ProblemDetail body = ProblemDetailsUtils.problemDetail(HttpStatus.BAD_REQUEST, "User does not belong to this company", "", request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
            var servletReq = ((org.springframework.web.context.request.ServletWebRequest) request).getRequest();

            var errors = ex.getBindingResult().getFieldErrors().stream()
                    .map(err -> java.util.Map.of(
                            "field", err.getField(),
                            "message", err.getDefaultMessage()))
                    .toList();

            ProblemDetail body = ProblemDetailsUtils.problemDetail(
                    HttpStatus.BAD_REQUEST,
                    "Validation failed",
                    "Request contains invalid fields",
                    servletReq
            );
            body.setProperty("errors", errors);

            return ResponseEntity.badRequest().body(body);
    }


}
