package ru.noleg.prreviewerservice.exception;

public class NoSuitableCandidatesException extends RuntimeException {
    private final ErrorCode code;

    public NoSuitableCandidatesException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getErrorCode() {
        return code;
    }
}
