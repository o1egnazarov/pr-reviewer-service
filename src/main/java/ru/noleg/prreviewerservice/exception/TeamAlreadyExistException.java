package ru.noleg.prreviewerservice.exception;

public class TeamAlreadyExistException extends RuntimeException {
    private final ErrorCode code;

    public TeamAlreadyExistException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getErrorCode() {
        return code;
    }
}
