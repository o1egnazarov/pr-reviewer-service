package ru.noleg.prreviewerservice.exception;

public class PrMergedException extends RuntimeException {
    private final ErrorCode code;

    public PrMergedException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getErrorCode() {
        return code;
    }
}
