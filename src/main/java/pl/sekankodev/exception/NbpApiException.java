package pl.sekankodev.exception;

public class NbpApiException extends AppException {
    public NbpApiException(String message) {
        super(message);
    }
    public NbpApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
