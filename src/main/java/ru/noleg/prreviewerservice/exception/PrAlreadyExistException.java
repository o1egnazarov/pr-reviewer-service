package ru.noleg.prreviewerservice.exception;

public class PrAlreadyExistException extends RuntimeException {
    private final ErrorCode code;

    public PrAlreadyExistException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getErrorCode() {
        return code;
    }
}
