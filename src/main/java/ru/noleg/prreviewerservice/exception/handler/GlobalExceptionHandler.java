package ru.noleg.prreviewerservice.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.noleg.api.models.ErrorResponse;
import ru.noleg.api.models.ErrorResponseError;
import ru.noleg.prreviewerservice.exception.NoAssignedForPrException;
import ru.noleg.prreviewerservice.exception.NoSuitableCandidatesException;
import ru.noleg.prreviewerservice.exception.NotFoundException;
import ru.noleg.prreviewerservice.exception.PrAlreadyExistException;
import ru.noleg.prreviewerservice.exception.PrMergedException;
import ru.noleg.prreviewerservice.exception.TeamAlreadyExistException;
import ru.noleg.prreviewerservice.exception.UserAlreadyExistException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            new ErrorResponse(
                new ErrorResponseError(ErrorResponseError.CodeEnum.NOT_FOUND, ex.getMessage())));
  }

  @ExceptionHandler(TeamAlreadyExistException.class)
  public ResponseEntity<ErrorResponse> handleTeamExistsException(TeamAlreadyExistException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            new ErrorResponse(
                new ErrorResponseError(ErrorResponseError.CodeEnum.TEAM_EXISTS, ex.getMessage())));
  }

  @ExceptionHandler(PrAlreadyExistException.class)
  public ResponseEntity<ErrorResponse> handlePrExistsException(PrAlreadyExistException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            new ErrorResponse(
                new ErrorResponseError(ErrorResponseError.CodeEnum.PR_EXISTS, ex.getMessage())));
  }

  @ExceptionHandler(PrMergedException.class)
  public ResponseEntity<ErrorResponse> handlePrMergedException(PrMergedException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            new ErrorResponse(
                new ErrorResponseError(ErrorResponseError.CodeEnum.PR_MERGED, ex.getMessage())));
  }

  @ExceptionHandler(NoSuitableCandidatesException.class)
  public ResponseEntity<ErrorResponse> handleNoSuitableCandidatesException(
      NoSuitableCandidatesException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            new ErrorResponse(
                new ErrorResponseError(ErrorResponseError.CodeEnum.NO_CANDIDATE, ex.getMessage())));
  }

  @ExceptionHandler(NoAssignedForPrException.class)
  public ResponseEntity<ErrorResponse> handleNotAssignedForPrException(
      NoAssignedForPrException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            new ErrorResponse(
                new ErrorResponseError(ErrorResponseError.CodeEnum.NOT_ASSIGNED, ex.getMessage())));
  }

  @ExceptionHandler(UserAlreadyExistException.class)
  public ResponseEntity<ErrorResponse> handleUserExistsException(UserAlreadyExistException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            new ErrorResponse(
                new ErrorResponseError(ErrorResponseError.CodeEnum.USER_EXISTS, ex.getMessage())));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleCommonException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            new ErrorResponse(
                new ErrorResponseError(
                    ErrorResponseError.CodeEnum.UNKNOWN_ERROR, ex.getMessage())));
  }
}
// "Unexpected error, please try again later"
