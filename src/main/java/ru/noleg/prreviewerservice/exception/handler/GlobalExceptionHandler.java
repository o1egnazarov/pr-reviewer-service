package ru.noleg.prreviewerservice.exception.handler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.noleg.api.models.ErrorResponse;
import ru.noleg.api.models.ErrorResponseError;
import ru.noleg.prreviewerservice.exception.DomainException;
import ru.noleg.prreviewerservice.exception.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                                new ErrorResponseError(ErrorResponseError.CodeEnum.NOT_FOUND, ex.getMessage())
                        )
                );
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                                new ErrorResponseError(ErrorResponseError.CodeEnum.NOT_FOUND, ex.getMessage())
                        )
                );
    }
}
