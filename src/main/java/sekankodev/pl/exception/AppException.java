package sekankodev.pl.exception;

public abstract class AppException extends RuntimeException {
    public AppException(String message) {
        super(message);
    }
    protected AppException(String message, Throwable cause) { super(message, cause); }
}