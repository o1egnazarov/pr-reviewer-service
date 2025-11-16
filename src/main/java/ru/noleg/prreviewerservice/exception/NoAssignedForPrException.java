package ru.noleg.prreviewerservice.exception;

public class NoAssignedForPrException extends RuntimeException {
  private final ErrorCode code;

  public NoAssignedForPrException(ErrorCode code, String message) {
    super(message);
    this.code = code;
  }

  public ErrorCode getErrorCode() {
    return code;
  }
}
