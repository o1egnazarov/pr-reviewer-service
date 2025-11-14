package ru.noleg.prreviewerservice.exception;

public class UserAlreadyExistException extends RuntimeException {
    private final ErrorCode code;

    public UserAlreadyExistException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getErrorCode() {
        return code;
    }
}
